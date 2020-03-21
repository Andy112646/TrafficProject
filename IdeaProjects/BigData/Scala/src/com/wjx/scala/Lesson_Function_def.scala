package com.wjx.scala

import java.util.Date


/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  */


/**
  * 方法名  参数1  参数1类型    参数2  参数2类型   返回值类型
  * def fun_name(para1: para1_type, para2: para2_type): return_type = {....}
  *
  * 1、方法定义：
  * 必须使用 def
  * （1）方法体中最后返回值可以使用return，如果使用了return，那么方法的返回值类型一定要指定
  * （2）方法体的return可以省略，默认将方法体中最后一次计算的结果当做返回值返回
  * （3）定义方法传入的参数一定要指定类型
  * （4）方法的方法体如果可以一行搞定，那么方法体的“{...}”可以省略
  * （5）如果定义方法时，省略方法名称和方法体之间的“=”，
  * 那么无论方法体最后一行计算的结果是什么，都会被丢弃，返回空Unit
  *
  */
object Lesson_Function_def {
  def main(args: Array[String]): Unit = {

    /**
      * 1、方法定义
      *
      */
    def max(a: Int, b: Int): Int = {
      if (a >= b) {
        a
      } else {
        b
      }
    }

    // 可以去掉花括号 写成一行
    // def max(a: Int, b: Int): Int = if (a >= b) a else b
    val i: Int = max(20, 50)
    println(i)


    /**
      * 2、递归方法
      * 需要显式给出返回值类型
      * （因为无法推断出后面递归调用的是否是同一个返回值类型）
      *
      */
    // 阶乘
    def fun01(num: Int): Int = {
      if (num == 1) {
        1
      } else {
        num * fun01(num - 1)
      }
    }

    println(fun01(5))


    /**
      * 3、参数有默认值的方法
      * 调用时可以不传有默认值的参数，也可以指定传入某个参数
      * （1）如果传入的参数个数与函数定义相同，则传入的数值会覆盖默认值
      * （2）如果不想覆盖默认值，传入的参数个数小于定义的函数的参数，则需要指定参数名称
      *
      */
    //    def fun02(a: Int = 10, b: Int = 20) = {
    //      a + b
    //    }

    //    println(fun02())
    //    println(fun02(5, 6))
    //    println(fun02(b = 30))


    /**
      * 4、可变长参数的方法
      * 在参数类型后面写个*
      * => 表示匿名函数
      **/
    def fun03(s: String*) = {
      // （1）直接输出集合
      println(s)

      // （2）for循环遍历输出
      for (elem <- s) {
        println(elem)
      }

      // （3）foreach 使用匿名函数的方式
      s.foreach(elem => {
        println(elem)
      })

      // （4）foreach 匿名函数简化版的方式
      // 因为上面那种方式的是同一个的参数只使用了一次
      s.foreach(println(_))

      // （5）如果只有一个参数遍历，还可以继续省略
      s.foreach(println)
    }

    fun03("a", "bb", "ccc")


    /**
      * 5、匿名函数（方法）
      * 多用于方法的参数是函数时
      *
      * 结构：
      * 需要处理的参数  处理了标识  方法体
      * (prepare_paras)   =>   {...}
      *
      * （1）有参匿名函数
      * （2）无参匿名函数
      * （3）有返回值的匿名函数
      * --可以将匿名函数返回给定义的一个变量
      * --匿名函数不能显示声明函数的返回类型
      */

    // 有参数匿名函数
    val value1 = (a: Int) => {
      println(a)
    }
    value1(100)

    //无参数匿名函数
    val value2 = () => {
      println("无参匿名函数")
    }
    value2()

    //有返回值的匿名函数
    val value3 = (a: Int, b: Int) => {
      a + b
    }
    println(value3(4, 4))


    /**
      * 6、嵌套函数
      * 例如：嵌套函数求5的阶乘
      */
    def fun5(num: Int) = {
      def fun6(a: Int): Int = {
        if (a == 1) {
          1
        } else {
          a * fun6(a - 1)
        }
      }

      fun6(num)
    }

    println(fun5(5))

    /**
      * 7、偏应用函数
      * 偏应用函数是一种表达式，不需要提供函数需要参数，或者只需要提供部分参数
      *
      * 应用场景：
      * 当方法中参数非常多，该方法的调用非常频繁，但每次调用只有固定的某个或几个参数变化，其他都不变
      * 则可以使用偏应用来简化
      *
      */
    def showLog(date: Date, log: String) = {
      println(s"Date is $date , log is $log")
    }
    // 不使用偏应用函数，每次调用都要输入相同的date
    val date = new Date()
    showLog(date, log = "a")
    showLog(date, log = "b")
    showLog(date, log = "c")

    // 使用偏应用函数,不变的参数写前面，变化的用下划线代替同时给出其类型
    def fun7 = showLog(date, _: String)

    fun7("aaa")
    fun7("bbb")
    fun7("ccc")


    /**
      * 8、高阶函数（方法），有三种
      * （1）方法的参数是函数
      * （2）方法的返回类型是函数
      * （3）方法的参数和方法的返回类型都是函数
      */
    // （1）方法的参数是函数
    def hightFun(f: (Int, Int) => Int, a: Int): Int = {
      f(a, 100)
    }

    def f(v1: Int, v2: Int): Int = {
      v1 + v2
    }

    println(hightFun(f, 200))

    // （2）方法的返回类型是函数
    // 1，2,3,4相加
    def hightFun2(a: String): (String, String) => String = {
      def f2(v1: String, v2: String): String = {
        v1 + "~" + v2 + "#" + a
      }

      f2
      // 后面加下划线表示将方法f2直接返回，此时hightFun2可以不用写返回类型
      // f2 _
    }

    // hightFun2方法需要一个参数，然后返回了一个函数f2（即f2()等同于hightFun2("aaa")()）
    // f2 需要两个参数，于是再接上("bbb", "ccc")
    println(hightFun2("aaa")("bbb", "ccc"))


    //（3）方法的参数和方法的返回类型都是函数
    def fun8(f: (Int, Int) => Int): (String, String) => String = {
      val i = f(1, 2)

      def fun9(s1: String, s2: String): String = {
        s1 + "@" + s2 + "*" + i
      }

      fun9
    }

    println(fun8((a, b) => {
      a + b
    })("hello", "world"))
    // 也可以简写成这样
    println(fun8(_+_)("hello", "world"))


    /**
      * 9、科里化函数
      * (可以理解为高阶函数的简化)
      *
      */
    def fun9(a: Int, b: Int)(c: Int, d: Int) = {
      a + b + c + d
    }

    println(fun9(1, 2)(3, 4))


  }
}
