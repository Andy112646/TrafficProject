package com.wjx.scala

/**
  * 用关键字case修饰的类就是样例类
  * 样例类是种特殊的类：
  * 1、实现了类构造参数的getter方法（构造参数默认被声明为val），当构造参数是声明为var类型的，它将帮你实现setter和getter方法
  * 2、样例类默认实现了toString,equals，copy和hashCode等方法。
  * 3、样例类可以new, 也可以不用new
  */

// 样例类
case class Person1(name: String, age: Int)

case class Human(var langue: String){

}

object Lesson_CaseClass {
  def main(args: Array[String]): Unit = {
    val p1 = new Person1("zhangsan", 10)
    val p2 = Person1("lisi", 20)
    val p3 = Person1("wangwu", 30)
    val h = Human("Chinese")
    println(p1.age)

    // var的参数默认增加了setter方法
    h.langue="English"
    println(h.langue)

    val list = List(p1, p2, p3)
    list.foreach { x => {
      x match {
        case Person1("zhangsan", 10) => println("zhangsan")
        case Person1("lisi", 20) => println("lisi")
        case _ => println("no match")
      }
    }
    }

  }
}

