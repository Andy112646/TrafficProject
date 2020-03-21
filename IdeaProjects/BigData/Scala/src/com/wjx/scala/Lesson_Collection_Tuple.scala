package com.wjx.scala

/**
  * @Auther:wjx
  * @Date:2019 /7/17
  * @Description:com.wjx.scala
  * @version:1.0
  */

/**
  * 1.元组定义
  *   与列表一样，与列表不同的是元组可以包含不同类型的元素，每个元素都可以指定各自的类型(当然，list指定为Any类型时，也能存各种类型，统称为Any类型)
  *   元组的值是通过将单个的值包含在圆括号中构成的。
  *
  * 2.创建元组与取值
  *   val  tuple = new Tuple（1） 可以使用new
  *   val tuple2  = Tuple（1,2） 可以不使用new，也可以直接写成val tuple3 =（1,2,3）
  *
  *   取值用”._XX” 可以获取元组中的值
  *
  *   注意：scala 中 tuple最多支持22个参数
  *         scala 中 tuple下标从1开始
  */
object Lesson_Collection_Tuple {
  def main(args: Array[String]): Unit = {


    /**
      * 1、元组的创建
      * 两种创建方式
      *
      * 最多支持22个元素
      */
    val tuple1: (Int, String, Boolean) = Tuple3(200, "abc", false)
    val tuple2 = Tuple2("zhangsan", 2)
    val tuple3 = Tuple3(1, 2, 3)
    val tuple4 = (1, 2, 3, 4)
    val tuple18 = Tuple18(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18)

    val tuple22 = new Tuple22(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22)
    // 获取元组元素
    println(tuple2._1 + "\t" + tuple2._2)
    val t = Tuple2((1, 2), ("zhangsan", "lisi"))
    println(t._2._1)

    /**
      * 2、元组遍历(productIterator 得到迭代器再边丽丽)
      *
      * （1）while
      * （2）foreach
      */
    val iterator: Iterator[Any] = tuple1.productIterator
    while (iterator.hasNext){
      println(iterator.next())
    }

    val ite: Iterator[Any] = tuple2.productIterator
    ite.foreach(println)

    /**
      * 3、swap方法
      * 调换顺序，只有Tuple2有
      *
      */
    println(tuple2.swap)

    /**
      * 4、toString
      */
    val str: String = tuple1.toString()
    println(str)

  }
}
