package com.zto56.kyzfs.interceptor
import android.util.Log
/**
 * @author gaolei46@ztoky.cn
 * 2023/5/19 15:05
 */
class DefaultLogPrinter: IPrinter {
 override fun print(priority: Priority, tag: String, msg: String) {
  Log.println(priority.toInt(), tag, msg)
 }
}