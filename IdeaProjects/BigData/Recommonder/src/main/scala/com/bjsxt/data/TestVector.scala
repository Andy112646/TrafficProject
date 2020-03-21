package com.bjsxt.data

import org.apache.spark.mllib.linalg.SparseVector

object TestVector {
  def main(args: Array[String]): Unit = {
//    while(true){
//      
//    	println(2*(2*random()).toInt)
//    }
//    println(2*(2*random()).toInt-1)
    
    
//    val conf = new SparkConf().setMaster("local").setAppName("test")
//    val sc = new SparkContext(conf)
//    val rdd = sc.makeRDD(List("bjsxt","shsxt","gzsxt"))
//    val result = rdd.zipWithIndex();
//    result.foreach(println)
//    val zipWithIndexRDD = rdd.zipWithIndex()
//    zipWithIndexRDD.foreach(println)
    
    val array: Array[Int] = Array.fill(3)(100)
    array.foreach(println)
    
    val vector = new SparseVector(10,Array(1,3,5),Array(100,200,300))
    println(vector.toDense)
    
    
    //zip  
//    val rdd1 = sc.makeRDD(1 to 10)
//    val rdd2 = sc.makeRDD(101 to 110)
//    val rdd3 = rdd1.zip(rdd2)
//    rdd3.foreach(println)
    
//    sc.stop()
  }
}