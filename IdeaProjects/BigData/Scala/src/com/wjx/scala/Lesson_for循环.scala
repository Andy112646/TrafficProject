package com.wjx.scala

/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  */

/** 和python一样，没有自增（++），自减（--）操作符
  * 不过有 += -= *= /= 。。。。。。。
  *
  * if-else 语法结构和java一样
  * while; do...while... 语法结构和java一样
  *
  */

object Lesson_for循环 {

  def main(args: Array[String]): Unit = {

    /**
      * for 循环
      * 1 to 5 表示一个range集合，左右都是闭区间
      * 1 until 5 也是range集合，左闭右开
      */
    var i = 1 to 5 // Range(1, 2, 3, 4, 5)
    var k = 1.to(5) // 等价于上面那种写法
    var j = 1.to(10, 2) // Range(1, 3, 5, 7, 9)   --1~10,步长为2
    var m = 1 until 5 // Range(1, 2, 3, 4)
    var n = 1.until(10, 2) // Range(1, 3, 5, 7, 9)  --1~10,步长为2
    println(j)
    println(n)

    // 九九乘法表(条件判断分两次写)
    for (i <- 1 to 9) {
      for (j <- 1 to i) {
        // print(j + "*" + i + "=" + i * j + "\t")
        // 写s表示可以将变量写入字符串中，$表示变量
        print(s"$j*$i=" + i * j + "\t")
      }
      println()
    }

    // 九九乘法表（条件判断写在一起）
    for (i <- 1 to 9; j <- 1 to i) {
      print(j + "*" + i + "=" + i * j + "\t")
      if (i == j) {
        println()
      }
    }

    // for 循环的条件判断还可以写if语句等
    // 多个条件可以用分号隔开也可以用空格隔开
    for(i <- 1 to 20; if (i>10); if(i%2==0)){
      print(i+"\t")
    }

    // yield 关键字，将所有满足条件的值组成一个vector集合
    val res = for(i <- 1 to 20; if (i>10); if(i%2==0)) yield i  // Vector(12, 14, 16, 18, 20)
    print(res)


  }
}
