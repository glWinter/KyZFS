package com.zto56.kyzfs.interceptor

/**
 * @author gaolei46@ztoky.cn
 * 2023/5/19 15:04
 */
enum class Priority(private val priority: Int){

 V(2),
 D(3),
 I(4),
 W(5),
 E(6),
 A(7);

 fun toInt(): Int {
  return priority
 }
}