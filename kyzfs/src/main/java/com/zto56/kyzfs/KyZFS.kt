package com.zto56.kyzfs

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.permissionx.guolindev.PermissionX

/**
 * type 0 = 相机
 * type 1 = 相册
 * type 2 = 文件选择
 * @author gaolei46@ztoky.cn
 * 2023/5/26 14:05
 */
class KyZFS(
    val activity: FragmentActivity, var maxItem: Int = 3,
    var maxSize: Int = 70 * 1024,
    var selectedSize: Int = 0,
    var type: Int = 1,
    var chooseVideo: Boolean = false,
    var authorization: String = "",
    var refreshToken: String = "",
    var url: String = "",
    var waterMaker: String = "",
    var isShowWaterMaker: Boolean = false,
    var isPrivate: Boolean = false
) {

    private constructor(activity: FragmentActivity,builder: Builder) : this(activity,
        builder.maxItem,
        builder.maxSize,
        builder.selectedSize,
        builder.type,
        builder.chooseVideo,
        builder.authorization,
        builder.refreshToken,
        builder.url,
        builder.waterMaker,
        builder.isShowWaterMaker,
        builder.isPrivate
    )

    private val FRAGMENT_TAG = "permission_fragment"
    private val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    class Builder {
        var maxItem = 3
            private set
        var maxSize = 70 * 1024
            private set
        var selectedSize = 0
            private set
        var type = 1
            private set
        var chooseVideo = false
            private set
        var authorization = ""
            private set
        var refreshToken = ""
            private set
        var url = ""
            private set
        var waterMaker = ""
            private set
        var isShowWaterMaker = false
            private set
        var isPrivate = false
            private set

        fun build(activity: FragmentActivity) = KyZFS(activity,this)
        fun setIsPrivate(isPrivate: Boolean) = apply {
            this.isPrivate = isPrivate
        }

        fun setIsShowWaterMaker(isShowWaterMaker: Boolean) = apply {
            this.isShowWaterMaker = isShowWaterMaker
        }

        fun setWaterMaker(waterMaker: String) = apply {
            this.waterMaker = waterMaker
        }

        fun setUrl(url: String) = apply {
            this.url = url
        }

        fun setRefreshToken(refreshToken: String) = apply {
            this.refreshToken = refreshToken
        }

        fun setMaxItem(maxItem: Int) = apply {
            this.maxItem = maxItem
        }

        fun setMaxSize(maxSize: Int) = apply {
            this.maxSize = maxSize
        }

        fun setSelectSize(selectedSize: Int) = apply {
            this.selectedSize = selectedSize
        }

        fun setType(type: Int) = apply {
            this.type = type
        }

        fun setChooseVideo(chooseVideo: Boolean) = apply {
            this.chooseVideo = chooseVideo
        }

        fun setAuthorization(authorization: String) = apply {
            this.authorization = authorization
        }

    }

    fun start(cb: KyZFSCb) {
        PermissionX.init(activity).permissions(permissions).request { allGranted, _, _ ->
            if (allGranted) {
                activity.runOnUiThread {
                    getZFSFragment().request(this, cb)
                }
            } else {
                T.t(activity, "未授予相关必要权限")
            }
        }
    }

    private fun getZFSFragment(): ZFSFragment {
        val fragmentManager: FragmentManager = activity.supportFragmentManager;
        val existedFragment: Fragment? = fragmentManager.findFragmentByTag(FRAGMENT_TAG)
        return if (existedFragment != null) {
            existedFragment as ZFSFragment
        } else {
            val permissionFragment = ZFSFragment()
            try {
                fragmentManager.beginTransaction().add(permissionFragment, FRAGMENT_TAG)
                    .commitNowAllowingStateLoss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            permissionFragment
        }
    }
}