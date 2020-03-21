package com.wjx.scalaspark.core

import org.apache.spark.util.AccumulatorV2
import org.apache.spark.{SparkConf, SparkContext}
/**
  * 样例类
  */
case class CountInfo(var totalCount: Int, var totalAge: Int)

/**
  * 自定义累加器
  * 必须继承抽象类 AccumulatorV2 实现相应方法
  * 进来的是 CountInfo 类型，出去的是 CountInfo 类型
  */
class SelfAccumulator extends AccumulatorV2[CountInfo, CountInfo] {

  // 初始化累加器的值，这个值最后要在merge合并的时候累加到最终结果
  private var result = new CountInfo(0, 0)

  /**
    *判断每个分区内的初始值
    *
    * 返回累加器是否是零值，Int类型： 0 就是零值，List 类型：Nil就是零值。
    * 这里判断时，要与方法reset() 初始值一直，初始判断时要返回true，内部会在每个分区内自动调用判断
    */
  override def isZero: Boolean = {
    result.totalCount == 0 && result.totalAge == 0
  }

  /**
    * 复制一个新的累加器，在这里就是如果用到了就会复制一个新的累加器
    */
  override def copy(): AccumulatorV2[CountInfo, CountInfo] = {
    val newAccumulator = new SelfAccumulator()
    newAccumulator.result = this.result
    newAccumulator
  }

  /**
    * 重置 AccumulatorV2 中的数据，这里初始化的数据是在RDD每个分区内部，每个分区内的初始值
    */
  override def reset(): Unit = {
    result = new CountInfo(0, 0)
  }

  /**
    * 每个分区累加数据
    * 这里是拿着初始的result值和每个分区的数据累加
    */
  override def add(v: CountInfo): Unit = {
    result.totalCount += v.totalCount
    result.totalAge += v.totalAge
  }

  /**
    * 将各个分区的累加结果进行累加
    * 这里拿着初始的result值，和每个分区最终的结果累加
    */
  override def merge(other: AccumulatorV2[CountInfo, CountInfo]): Unit = other match {
    case o: SelfAccumulator => {
      result.totalCount += o.result.totalCount
      result.totalAge += o.result.totalAge
    }
    case _ => throw new UnsupportedOperationException(
      s"Cannot merge ${this.getClass.getName} with ${other.getClass.getName}"
    )
  }

  /**
    * 累加器对外返回的结果
    */
  override def value: CountInfo = result
}


/**
  * main 方法，测试自定义累加器
  */
object DefindSelfAccumulator {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    conf.setMaster("local")
    conf.setAppName("DefindSelfAccumulator")
    val sc = new SparkContext(conf)

    val nameList = sc.parallelize(List[String](
      "A 1", "B 2", "C 3", "D 4", "E 5",
      "F 6", "G 7", "H 8", "I 9"
    ), 3)

    /**
      * 初始化累加器
      */
    val accumulator = new SelfAccumulator()
    sc.register(accumulator, "Self Defined accumulator") //注册累加器

    val transInfo = nameList.map(one => {
      val countInfo = CountInfo(1, one.split(" ")(1).toInt)
      accumulator.add(countInfo) // 添加对象至累加器
      countInfo // 返回对象
    })
    transInfo.count()

    println(s"totalCount = ${accumulator.value.totalCount}, totalAge = ${accumulator.value.totalAge}")


  }

}


