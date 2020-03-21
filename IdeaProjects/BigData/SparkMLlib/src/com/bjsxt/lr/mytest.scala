package com.bjsxt.lr

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.regression.LinearRegressionWithSGD

object mytest {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("test")
    val sc = new SparkContext(conf)
    val data = sc.textFile("./lpsa.data")
    println("data partitions length = "+data.partitions.length)
    val labeledData = data.map(line=>{
      val y = line.split(",")(0)
      val x = line.split(",")(1)
      LabeledPoint(y.toDouble,Vectors.dense(x.split(" ").map(_.toDouble)))
    })
    
    val arrLabeledData  = labeledData.randomSplit(Array(0.8,0.2), 1)
    
    val linearRegressionWithSGD = new LinearRegressionWithSGD()
    linearRegressionWithSGD.setIntercept(true)
    linearRegressionWithSGD.optimizer.setStepSize(1)
    linearRegressionWithSGD.optimizer.setNumIterations(100)
    linearRegressionWithSGD.optimizer.setMiniBatchFraction(1)
    
    val model  = linearRegressionWithSGD.run(arrLabeledData(0))   
    println(model.weights)
    println(model.intercept)
    
    val predictResult = model.predict(arrLabeledData(1).map(labeledPoint=>{labeledPoint.features}))
    
    val predict_fact = predictResult.zip(arrLabeledData(1).map(labelPoint=>{labelPoint.label}))
    val totalError = predict_fact.map{case (p,v)=>{ Math.abs(p-v) }}.reduce(_+_)
    val avgError = totalError/arrLabeledData(1).count()
    println("error = "+avgError)
    
    
  }
}