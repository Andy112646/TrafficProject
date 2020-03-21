package com.wjx.scala

/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  */

/**
  * 1.创建set
  * 注意：set集合会自动去重
  *
  * 2.set遍历
  * foreach，for
  *
  * 3.set方法举例
  * 交集:intersect , &
  * 差集: diff , &~
  * 子集: subsetOf
  * 过滤: filter
  * 最大: max
  * 最小: min
  * 转成数组、集合: toArray toList
  * 转成字符串: mkString(“~”)
  *
  */

object Lesson_Collection_Set {
  def main(args: Array[String]): Unit = {
    /**
      * 1、创建set
      * 会自动去重
      */
    val set1 = Set[Int](1, 2, 3, 4, 4)
    val set2 = Set[Int](1, 2, 5)

    /**
      * 2、遍历set
      * 注意：set会自动去重
      */
    set1.foreach {
      println
    }
    for (s <- set1) {
      println(s)
    }
    println("*******")


    /**
      * 3、set有关的方法举例
      */
    // 交集 intersect
    val set3 = set1.intersect(set2)
    set3.foreach {
      println
    }
    // 操作符操作求交集
    val set5 = set1 & set2
    val set4 = set1.&(set2)
    set4.foreach {
      println
    }
    println("*******")

    // 差集 diff
    set1.diff(set2).foreach {
      println
    }
    // 操作符操作求差集
    (set1 &~ set2).foreach {
      println
    }
    set1.&~(set2).foreach {
      println
    }

    // 子集
    set1.subsetOf(set2)
    // 最大值
    println("最大值：" + set1.max)
    // 最小值
    println("最小值：" + set1.min)
    println("****")

    // 转成数组、list
    set1.toArray.foreach {
      println
    }
    println("****")
    set1.toList.foreach {
      println
    }

    // mkString
    println(set1.mkString)
    println(set1.mkString("\t"))


    /**
      * 4、可变长的set
      * 需要导包，然后不可变长的就变成可变长的了
      *
      */
    // 可变的set
    import scala.collection.mutable
    var sc = mutable.Set[Int](1, 2, 3)


    sc.+(100) // 只是返回一个新的set集合，原来的set元素没有改变
    sc.+=(200) // 原来的set增加了一个元素200
    sc.add(6)
    sc.foreach(println)

    // 不可变set
    import scala.collection.immutable
    var sc2 = immutable.Set[Int](1, 2, 3)
  }
}
