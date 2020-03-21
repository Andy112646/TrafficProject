package com.wjx.scala

import scala.collection.mutable.ArrayBuffer

/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  */
/**
  * 1.	创建数组
  *   new Array[Int](10)
  *   赋值：arr(0) = xxx
  *   Array[String](“s1”,”s2”,”s3”)
  * 2.	数组遍历
  *   for
  *   foreach
  * 3.	创建、遍历二维数组
  * 4.	数组中方法举例
  *   Array.concate：合并数组
  *   Array.fill(5)(“bjsxt”)：创建初始值的定长数组
  *
  */

object Lesson_Collection_Array {
  def main(args: Array[String]): Unit = {

    /**
      * 1、创建数组两种方式：
      * （1）new Array[String](3)
      * （2）直接Array
      */
    // 创建类型为Int 长度为3的数组
    val arr1 = new Array[Int](3)
    // 创建String 类型的数组，直接赋值
    val arr2 = Array[String]("s100", "s200", "s300")
    // 赋值
    arr1(0) = 100
    arr1(1) = 200
    arr1(2) = 300

    /**
      * 2、遍历数组的两种方式
      *
      */
    // for循环
    for (i <- arr1) {
      println(i)
    }

    // foreach
    arr2.foreach(println)
    arr2.foreach(println(_))
    arr2.foreach(s => {
      println(s)
    })

    /**
      * 创建二维数组和遍历
      */
    val arr3 = new Array[Array[String]](3)
    arr3(0) = Array("1", "2", "3")
    arr3(1) = Array("4", "5", "6", "6")
    arr3(2) = Array("7", "8", "9")
    // for循环遍历
    for (i <- 0 until arr3.length) {
      for (j <- 0 until arr3(i).length) {
        print(arr3(i)(j) + "	")
      }
      println()
    }
    // foreach 遍历
    arr3.foreach(arr => {
      arr.foreach(print)
    })


    val arr4 = Array[Array[Int]](Array(1, 2, 3), Array(4, 5, 6))
    arr4.foreach { arr => {
      arr.foreach(i => {
        println(i)
      })
    }
    }
    println("-------")
    for (arr <- arr4; i <- arr) {
      println(i)
    }

    /**
      * 2、可变数组
      *
      */
    var arr5 = ArrayBuffer[Int](1, 2, 3)
    arr5.+=(999) // 将元素添加到数组末尾
    arr5.+=:(666) // 将元素添加到数组最前面
    arr5.append(7,8,9) //在数组末尾追加多个元素
    arr5.foreach(println)


  }

}
