package com.wjx.scala

/**
  * Match 模式匹配
  * 1、case _ 默认匹配,放在最后（相当于java-switch的default）
  * 如果都没匹配上则匹配 case _ 选项
  *
  * 2、模式匹配不仅仅可以匹配值，还可以匹配类型
  *
  * 3、匹配过程中会有数值的转换，例如Double类型的 1.0 也可匹配上 Int的 1
  *
  * 4、从上往下匹配，一旦匹配成功就不再往下继续匹配
  *
  * 5、模式匹配外部的 花括号 可以省略
  *
  */
object Lesson_Match {
  def main(args: Array[String]): Unit = {
    val tp = (1, 3.14, "abc", 'a', true)
    val iter: Iterator[Any] = tp.productIterator
    iter.foreach(elem => MatchTest(elem))
    // 简写
    // iter.foreach(MatchTest)
  }

  /**
    * 模式匹配
    *
    * @param o
    */
  def MatchTest(o: Any) = {
    o match {
      case 1 => println("value is 1")
      case i: Int => println(s"type is Int, value = $i")
      case d: Double => println(s"type is Double, value = $d")
      case s: String => println(s"type is String, value = $s")
      case 'a' => println("value is a")
      case _ => println("no match...")
    }
  }
}
