package com.wjx.scala

/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  *
  * Scala:
  * 1、Scala object是单例对象，object中定义的全部是静态的（也就是说程序一运行就会加载object，执行object中的语句）
  * 2、Scala 中定义变量使用var，定义常量使用val，变量可变，常量不可变；
  * 尽量写val，因为val更容易被回收
  * 3、Scala 中每行后面都会有分号自动推断机制，可以不用写分号
  * 4、Scala 中有数据类型自动推断机制，所以变量和常量类型可省略
  * 5、Scala 中类可以传参（此时必须指定参数类型），给类传了参数就默认有了构造，类中的属性默认有getter和setter方法
  * 6、类中重写构造时，构造中第一行必须先调用默认的构造。def this(...){...}
  * 7、Scala 中当 new Class时，类中除了方法不执行（对应的构造方法还是会执行的），其他都执行
  * 8、在同一个Scala文件中，class名称和Object对象的名称一样时
  * 这个类叫做这个对象的伴生类，这个对象叫做这个类的伴生对象
  * 他们之间可以互相访问 private 对象（属性）
  * 9、Object对象不支持传参，但是里面可以定义 apply 方法，这时可以在main里面调用带参的Object对象
  * 10、apply 方法可以定义多个,调用时会根据调用的参数个数去找对应的apply方法（有点重载的感觉）
  *
  */

/**
  * 没有参数列表的类
  */
class Person {
  val name = "zhangsan"
  val age = 18

  def sayName() = {
    println("my name is " + name)
  }
}

/**
  * 有参数列表的类
  *
  * @param xname
  * @param xage
  */
class Person2(xname: String, xage: Int) {
  val name = xname
  var age = xage
  var gender = "male"

  // 重写构造方法，第一行必须先调用默认的构造方法
  def this(yname: String, yage: Int, ygender: String) {
    this(yname, yage)
    this.gender = ygender
  }

  def sayName(): Unit = {
    println("hello " + Lesson_ClassAndObj.name)
  }
}


object Lesson_ClassAndObj {
  val name = "wangwu" // 静态常量

  def apply(i: Int) = {
    print("apply--" + i)
  }

  def main(args: Array[String]): Unit = {
    val p = new Person
    var p2 = new Person2("lisi", 20)
    p.sayName()
    p2.sayName()
    println(p2.gender)
    val p3 = new Person2("zhaoliu", 23, "female")
    println(p3.gender)

    Lesson_ClassAndObj(666)
  }
}
