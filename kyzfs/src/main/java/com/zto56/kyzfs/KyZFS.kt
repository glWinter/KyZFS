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
class KyZFS(val activity: FragmentActivity) {

    var maxItem = 3
    var maxSize = 70 * 1024
    var selectedSize = 0
    var type = 1
    var chooseVideo = false
    var authorization = ""
    var refreshToken = ""
    var url = ""
    var waterMaker = ""
    var isShowWaterMaker = false
    var isPrivate = false
    private val FRAGMENT_TAG = "permission_fragment"
    private val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    fun start(cb:KyZFSCb) {
        PermissionX.init(activity).permissions(permissions).request { allGranted, _, _ ->
            if (allGranted) {
                activity.runOnUiThread {
                    getZFSFragment().request(this,cb)
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
                fragmentManager.beginTransaction().add(permissionFragment, FRAGMENT_TAG).commitNowAllowingStateLoss()
            }catch (e:Exception){
                e.printStackTrace()
            }
            permissionFragment
        }
    }
}