package com.bjsxt.lr

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.linalg.Vectors
import scala.collection.mutable.ArrayBuffer
import org.apache.spark.mllib.regression.LinearRegressionWithSGD


//age,sex,bmi,children,smoker,region,charges

object healthPredict {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("healthPredict")
    val sc = new SparkContext(conf)
    val data = sc.textFile("insurance.csv")
    //统计各区域的人数 southwest southeast northwest northeast
    val northeastCount = data.filter(one=>{
    	val local = one.split(",")(5)
    			local.equals("northeast")
    }).count()
    
    val northwestCount = data.filter(one=>{
    	val local = one.split(",")(5)
    			local.equals("northwest")
    }).count()
    
    val southeastCount = data.filter(one=>{
    	val local = one.split(",")(5)
    			local.equals("southeast")
    }).count()
    
    val southwestCount = data.filter(one=>{
      val local = one.split(",")(5)
      local.equals("southwest")
    }).count()
    
    println("northeast = " +northeastCount+"\t"+
            "northwest = "+northwestCount+"\t"+
            "southeast = "+southeastCount+"\t"+
            "southwest = "+southwestCount)
    
    //将第二列 “性别” 数据转换成数据  female 用0表示，male 用1表示
    val labeledPointData = data.map(one=>{
      val mutableArr = ArrayBuffer[Double]()
      val arr = one.split(",")
      
      val age = arr(0).toDouble
      val sex = transSex(arr(1)).toDouble
      val bmi = arr(2).toDouble
      val children = arr(3).toDouble
      val smoker = transSmoker(arr(4)).toDouble
      val region = transRegion(arr(5)).toDouble
      
      val charges = arr(6).toDouble
      mutableArr.append(age,sex,bmi,children,smoker,region)
      val resultArr = mutableArr.toArray
      LabeledPoint(charges,Vectors.dense(resultArr))
    })
    
    //将数据分为训练集 和 测试集
    val randomData = labeledPointData.randomSplit(Array(0.8,0.2), 100)
    val trainData = randomData(0)
    val testData = randomData(1)
    
    val linearRegressionWithSGD = new LinearRegressionWithSGD()
    //设置截距
    linearRegressionWithSGD.setIntercept(true)
    //设置步长
    linearRegressionWithSGD.optimizer.setStepSize(0.01)
    //每次迭代计算误差选取的样本数据集
    linearRegressionWithSGD.optimizer.setMiniBatchFraction(1)
    //设置迭代次数
    linearRegressionWithSGD.optimizer.setNumIterations(100)
    //设置迭代的误差
    linearRegressionWithSGD.optimizer.setConvergenceTol(0.01)
    
    //训练模型
    val lrmodel = linearRegressionWithSGD.run(trainData)
    println("lrmodel 截距="+lrmodel.intercept+"\t 权重参数="+lrmodel.weights)
    
    //预测值
    val predictValue = lrmodel.predict(testData.map(lp=>{lp.features}))
    
    val predict_fact = predictValue.zip(testData.map(lp=>{lp.label}))
    
    //预测值与真实值的总误差
    val totalError = predict_fact.map(tuple=>{
      Math.abs(tuple._1-tuple._2)
    }).reduce(_+_)
    
    val avgError =  totalError/testData.count()
    println("预测总条数 = "+testData.count()+",avgError = "+avgError)
  }
  
  def transSex(s:String)=
    s match {
      case "male"=>1
      case "female"=> 0
    }
  
  def transSmoker(s:String)={
    s match {
      case "yes"=>1
      case "no"=>0
    }
  }
  def transRegion(s:String)={
	  s match {
		  case "northeast"=>0
		  case "northwest"=>1
		  case "southeast"=>2
		  case "southwest"=>3
	  }
  }
  
  
}