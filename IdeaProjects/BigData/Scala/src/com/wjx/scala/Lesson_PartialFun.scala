package com.wjx.scala

/**
  * 偏函数，方法中没有match只有case，则这个函数可以定义成偏函数
  *
  * 1、偏函数定义时，不能使用括号传参，默认定义偏函数中传入一个值
  * 2、只能匹配一个值，匹配上了返回某个值（和switch很相似）
  *
  * PartialFunction[A, B]
  *  A是匹配的类型，B是匹配上后返回的类型
  *
  */
object Lesson_PartialFun {
  // 偏函数定义时，必须指定类型
  def MyTest :PartialFunction[String, Int] ={
    case "abc"=>1
    case "a" => 2
    case _ => 3
  }
  def main(args: Array[String]): Unit = {
    println(MyTest("abc"))
  }
}
