package com.wjx.scala

import scala.collection.mutable.ListBuffer

/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  */

/**
  *
  * 1.创建list,一般会指定list存储的元素的类型
  * val list = List(1,2,3,"abc",true) 因为有不同的类型，所以为Any类型
  * val list = List[Int](1,2,3,4)
  * Nil 是长度为0的list
  *
  * 2.list遍历
  * foreach ，for
  *
  * 3.list方法举例
  * filter:过滤元素，返回满足条件的
  * count:计算符合条件的元素个数
  * map：对元素操作
  * flatmap ：压扁扁平,先map再flat
  */
object Lesson_Collection_List {
  def main(args: Array[String]): Unit = {
    //创建
    val list = List[Int](1, 2, 3, 4, 5)

    //遍历
    for (elem <- list) {
      println(elem)
    }
    list.foreach { x => println(x) }

    /**
      * filter 方法，返回满足条件的
      */
    val list1 = list.filter { x => x > 3 }
    list1.foreach {
      println // 4 5
    }

    /**
      * count 方法：返回满足条件的条数
      */
    val value = list1.count { x => x > 3 }
    println(value)

    /**
      * map 方法:
      * 将list集合中的元素一个一个的处理，来一条出一条
      * 处理后生成的是list集合，里面存的是一个个数组
      */
    val nameList = List("hello bjsxt", "hello xasxt", "hello shsxt")
    // 将nameList的每个元素按空格切分
    // 将切分的结果赋值给mapResult，其类型为List[Array[String]]
    // 每个nameList的元素切分所生产的新元素后存储在Array数组中，每个Array又存储在List集合中
    val mapResult: List[Array[String]] = nameList.map { x => x.split(" ") }
    //mapResult.foreach {println}
    mapResult.foreach(arr => {
      println("新的数组：")
      arr.foreach(println)
    })

    /**
      * flatMap 方法:
      * 也是将元素一个一个处理，来一条出多条
      * 返回的是list集合，但是里面是string类型的元素
      *
      * 也就是说相比于map方法，flatMap多做了一步，就是继续将数组也拆出来了
      *
      */
    val flatMapResult: List[String] = nameList.flatMap { x => x.split(" ") }
    flatMapResult.foreach {
      println
    }

    /**
      * 2、可变的list
      *
      */
    var listBuf = ListBuffer[Int](1, 2, 3)
    listBuf.+=(6)
    listBuf.+=:(200)
    listBuf.append(4, 5)
    listBuf.foreach(println)

  }

}
