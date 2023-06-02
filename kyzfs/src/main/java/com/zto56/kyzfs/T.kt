package com.zto56.kyzfs

import android.content.Context
import android.widget.Toast

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/26 14:38
 */
class T {
    companion object{
        private var toast: Toast? = null
        fun t(context: Context, msg: String) {
            if (toast != null) {
                toast!!.setText(msg);
                toast!!.duration = Toast.LENGTH_LONG
                toast!!.show();
            } else {
                toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                toast!!.show();
            }
        }
    }

}