package com.wjx.scala

/**
  * Scala Trait(特征) 相当于 Java 的接口，实际上它比接口还功能强大。
  * 与接口不同的是，它还可以定义属性和方法的实现。
  * 一般情况下Scala的类可以继承多个Trait，从结果来看就是实现了多重继承。
  * Trait(特征) 定义的方式与类类似，但它使用的关键字是 trait。
  *
  * 注意：
  * 1、一个类继承trait时用extends，多个继承时后面的用with
  * 2、Trait不能传参
  * 3、继承的多个trait中如果有同名的方法和属性，必须要在类中使用“override”重新定义
  */
trait Read {
  val readType = "read"
  val gender = "m"

  def read(name: String) = {
    println(s"$name is reading...")
  }
}

trait Listen {
  val listenType = "listen"
  val gender = "f"

  def listen(name: String) = {
    println(s"$name is listen...")
  }
}

class Human() extends Read with Listen {
  override val gender = "f"
}


object Lesson_Trait {
  def main(args: Array[String]): Unit = {
    val human = new Human()
    human.read("andy")
    human.listen("lucy")
    println(human.listenType)
    println(human.readType)
    println(human.gender)
  }
}
