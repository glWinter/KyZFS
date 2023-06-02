package com.zto56.kyzfs.interceptor

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/19 15:05
 */
interface IPrinter {
 fun print(priority: Priority, tag: String, msg: String)
}