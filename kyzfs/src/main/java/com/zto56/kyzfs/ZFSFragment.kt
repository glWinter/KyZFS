package com.zto56.kyzfs

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.huantansheng.easyphotos.EasyPhotos
import com.huantansheng.easyphotos.constant.Type
import com.huantansheng.easyphotos.models.album.entity.Photo
import com.zto56.kyzfs.Const.Companion.REQUEST_CODE_FILE_SELECT
import com.zto56.kyzfs.FileUtils.imageForBase64
import com.zto56.kyzfs.FileUtils.saveBitmap2FileAsQuality
import com.zto56.kyzfs.luban.Luban
import com.zto56.kyzfs.zfs.http.FileUploadCallback
import com.zto56.kyzfs.zfs.http.HttpRepository
import com.zto56.kyzfs.zfs.model.GenericResponse
import com.zto56.kyzfs.zfs.model.UploadRequest
import com.zto56.kyzfs.zfs.model.UploadResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.rosuh.filepicker.bean.FileItemBeanImpl
import me.rosuh.filepicker.config.AbstractFileFilter
import me.rosuh.filepicker.config.FilePickerManager
import org.json.JSONObject
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/26 15:34
 */
class ZFSFragment : Fragment() {
    private lateinit var kyZFS: KyZFS
    private lateinit var cb: KyZFSCb
    private val compressedFiles = mutableListOf<FileInfo>()
    private var jsonObjects = mutableListOf<JSONObject>()
    private var uploadPhotoNum = 0
    private var totalSelectFileSize = 0L
    private var progressDialog: ProgressDialog? = null

    fun request(kyZFS: KyZFS, cb: KyZFSCb) {
        this.kyZFS = kyZFS
        this.cb = cb
        jsonObjects.clear()
        compressedFiles.clear()
        clear()
        operate()
    }

    private fun operate() {
        when (kyZFS.type) {
            1 -> EasyPhotos.createAlbum(this@ZFSFragment, false, false, ImageLoadEngine).apply {
                setFileProviderAuthority(requireActivity().packageName + ".fileProvider")
                setCount(kyZFS.maxItem)
                setVideo(kyZFS.chooseVideo)
                setGif(false)
                setPuzzleMenu(false)
                setCleanMenu(false)
            }.start(Const.REQUEST_CODE_ALBUM)
            0 -> EasyPhotos.createCamera(this@ZFSFragment, false).apply {
                setFileProviderAuthority(requireActivity().packageName + ".fileProvider")
            }.start(Const.REQUEST_CODE_CAMERA)
            2-> FilePickerManager
                .from(this)
                .maxSelectable(kyZFS.maxItem)
                .filter(object : AbstractFileFilter() {
                    override fun doFilter(listData: ArrayList<FileItemBeanImpl>): ArrayList<FileItemBeanImpl> {
                        return ArrayList(listData.filter { item ->
                            (item.isDir)||item.fileName.endsWith(".pdf")||item.fileName.endsWith(".xls")||
                                    item.fileName.endsWith(".xlsx")||item.fileName.endsWith(".xlsm")||
                                    item.fileName.endsWith(".doc")||item.fileName.endsWith(".docx")
                        })
                    }
                }).forResult(REQUEST_CODE_FILE_SELECT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == Const.REQUEST_CODE_ALBUM || requestCode == Const.REQUEST_CODE_CAMERA)&&data!=null) {
            val resultPhotos: List<Photo>? =
                data.getParcelableArrayListExtra(EasyPhotos.RESULT_PHOTOS)
            lifecycleScope.launch(Dispatchers.IO) {
                compress(resultPhotos!!)
                launch(Dispatchers.Main) {
                    initProgress()
                }
                Log.d("test", "total = $totalSelectFileSize")
                if (totalSelectFileSize + kyZFS.selectedSize < kyZFS.maxSize) {
                    uploadFile()
                } else {
                    launch(Dispatchers.Main) {
                        T.t(requireContext(), "选择文件超出大小限制")
                    }
                }
            }
        }else if(requestCode == REQUEST_CODE_FILE_SELECT&&data!=null){
            val list = FilePickerManager.obtainData(release = true)
            list.forEach {
                compressedFiles.add(FileInfo(File(it),"file"))
                totalSelectFileSize+=(File(it).length()/1024)
            }
            initProgress()
            lifecycleScope.launch(Dispatchers.IO){
                if (totalSelectFileSize + kyZFS.selectedSize < kyZFS.maxSize) {
                    uploadFile()
                } else {
                    launch(Dispatchers.Main) {
                        T.t(requireContext(), "选择文件超出大小限制")
                    }
                }
            }
        }
    }

    private fun compress(list: List<Photo>) {
        for (photo in list) {
            if (photo.type.contains(Type.VIDEO)) {
                compressedFiles.add(FileInfo(File(photo.path), "video"))
                totalSelectFileSize += photo.size / 1024
                continue
            }
            val files: List<File> =
                Luban.with(requireContext()).ignoreBy(400).load(photo.path).get()
            if (files.isNotEmpty()) {
                totalSelectFileSize += if (kyZFS.isShowWaterMaker) {
                    val bitmap = BitmapFactory.decodeFile(files[0].absolutePath).copy(Bitmap.Config.RGB_565, true)
                    val newPath: String = requireActivity().externalCacheDir?.absolutePath + "/news/waterMaker_" + files[0].name
                    val waterMarkBitmap: Bitmap = getWaterMarkBitmap(kyZFS.waterMaker)
                    val res = EasyPhotos.addWatermark(waterMarkBitmap, bitmap, waterMarkBitmap.width, 0, 0, true, 0)
                    if (!TextUtils.equals(files[0].absolutePath, photo.path)) {
                        FileUtils.deleteFiles(files[0].absolutePath)
                    }
                    val file = saveBitmap2FileAsQuality(res, newPath, 60)
                    compressedFiles.add(FileInfo(file,"image"));
                    file.length()/1024;
                } else {
                    compressedFiles.add(FileInfo(files[0], "image"))
                    files[0].length() / 1024
                }
            }
        }
    }
    private fun getWaterMarkBitmap(waterMaker:String):Bitmap{
        val strokeTextView = StrokeTextView(requireActivity()).apply {
            setTextColor(Color.parseColor("#ffffff"))
            text = waterMaker
            isDrawingCacheEnabled = true
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
            layout(0, 0, measuredWidth, measuredHeight);
        }
        return strokeTextView.drawingCache
    }
    private fun initProgress() {
        if (compressedFiles.size > 1) {
            progressDialog = ProgressDialog(kyZFS.activity).apply {
                setMessage("正在上传中")
                setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                setCancelable(false)
                isIndeterminate = false
                setCanceledOnTouchOutside(false)
                max = compressedFiles.size
                progress = uploadPhotoNum
            }
            progressDialog!!.show()
        } else if (compressedFiles.size == 1) {
            progressDialog = ProgressDialog(kyZFS.activity).apply {
                setMessage("正在上传中")
                setCancelable(false)
                isIndeterminate = false
                setCanceledOnTouchOutside(false)
            }
            progressDialog!!.show()
        }

    }

    private suspend fun uploadFile() {
        val countDownLatch = CountDownLatch(compressedFiles.size)
        try {
            val result = ZFSRequest.getUploadToken(kyZFS.url,kyZFS.refreshToken,kyZFS.authorization)
            HttpRepository.bindEnvironment(HttpRepository.Environment.PRODUCTION)
            for (compressedFile in compressedFiles) {
                var base64File: File?
                if (TextUtils.equals(compressedFile.type, "video")) {
                    val bitmap: Bitmap = getVideoThumb(compressedFile.file.absolutePath)
                    val newPath: String =
                        requireContext().externalCacheDir?.absolutePath + "/news/video_thumb_" + System.currentTimeMillis() + ".jpeg"
                    base64File = saveBitmap2FileAsQuality(bitmap, newPath, 60)
                    bitmap.recycle()
                } else {
                    base64File = compressedFile.file
                }

                val uploadRequest = UploadRequest(base64File, result.result.uploadToken).apply {
                    returnServerFileName = true
                    isPrivate = kyZFS.isPrivate
                }

                HttpRepository.getInstance().uploadFile(kyZFS.authorization,
                    result.result.appId,
                    uploadRequest,
                    object : FileUploadCallback<GenericResponse<UploadResult>> {
                        override fun onSuccess(data: GenericResponse<UploadResult>?) {
                            val jsonObject = JSONObject()
                            jsonObject.put("base64",  if(kyZFS.type == 2){""}else{imageForBase64(base64File)})
                            jsonObject.put("size", (compressedFile.file.length() / 1024).toString())
                            jsonObject.put("fileName", data!!.result.fileName)
                            jsonObject.put("realName", compressedFile.file.name)
                            jsonObject.put("group", data.result.group)
                            jsonObject.put("url", data.result.url)
                            if (compressedFile.type == "video") {
                                jsonObject.put("mp4FileUrl", compressedFile.file.absolutePath)
                            }
                            jsonObjects.add(jsonObject)
                            uploadPhotoNum++
                            countDownLatch.countDown()
                            lifecycleScope.launch(Dispatchers.Main) {
                                progressDialog!!.progress = uploadPhotoNum
                            }
                        }

                        override fun onFailure(error: Throwable?) {
                            countDownLatch.countDown()
                            uploadPhotoNum++
                            lifecycleScope.launch(Dispatchers.Main) {
                                progressDialog!!.progress = uploadPhotoNum
                                T.t(requireActivity(), "error = " + error!!.message)
                            }
                        }

                        override fun onProgressChange(bytesWritten: Long, contentLength: Long) = Unit

                    })

            }
            withContext(Dispatchers.IO) {
                countDownLatch.await(10,TimeUnit.SECONDS)
                launch(Dispatchers.Main) {
                    progressDialog?.dismiss()
                    cb.success(jsonObjects)
                }
            }
        }catch (e:MyRuntimeException){
            e.printStackTrace()
            countDownLatch.countDown()
            lifecycleScope.launch(Dispatchers.Main){
                T.t(requireContext(),e.msg)
            }
        }

    }

    private fun getVideoThumb(path: String): Bitmap {
        val media = MediaMetadataRetriever()
        media.setDataSource(path)
        return media.frameAtTime!!
    }

    private fun clear() {
        lifecycleScope.launch(Dispatchers.IO) {
            val file = File(requireActivity().externalCacheDir, "luban_disk_cache")
            FileUtils.deleteDirectory(file)
            val videoFile = File(requireActivity().externalCacheDir,"news")
            FileUtils.deleteDirectory(videoFile)
        }
    }

}
