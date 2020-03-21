package com.wjx.scala

/**
  * Trait 中可以有 实现的方法体 或者 未实现的方法体
  * 当类继承Trait是要实现未实现的方法
  *
  */

trait IsEquale{
  def isEqu(o:Any):Boolean
  def isNotEqu(o:Any):Boolean = {
    !isEqu(o)
  }
}

class Point(xx:Int, xy:Int) extends IsEquale {
  val x = xx
  val y = xy


  override def isEqu(o: Any): Boolean = {
    // 先判断参数是否是Point的实例
    // 如果是则将判断调用isEqu方法的对象的x是否和传进来的Point对象的x值相等
    o.isInstanceOf[Point] && o.asInstanceOf[Point].x==this.x
  }
}
object Lesson_Trait2 {
  def main(args: Array[String]): Unit = {
    val p1 = new Point(1, 2)
    val p2 = new Point(1, 3)
    println(p1.isEqu(p2))
    println(p1.isNotEqu(p2))
  }
}
