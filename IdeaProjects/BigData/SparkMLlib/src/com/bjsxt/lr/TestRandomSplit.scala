package com.bjsxt.lr

import org.apache.spark.{SparkConf, SparkContext}

object TestRandomSplit {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("TestRandomSplit")
    val sc = new SparkContext(config = conf)

    val arr = Array(1 ,2 ,3, 4, 5, 6, 7, 8, 9, 10)
    val rdd = sc.parallelize(arr)
    val temp = rdd.randomSplit(Array(0.7,0.3), seed = 10L)
    val (trainSet, testSet) = (temp(0), temp(1))
    trainSet.foreach(println(_))
    testSet.foreach(println(_))
  }
}
