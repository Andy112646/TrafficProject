package com.wjx.scala

/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  */
object Lesson_Collection_Map {
  def main(args: Array[String]): Unit = {
    /**
      * 1、map创建
      * 有两种书写方式
      * (1)  "1" -> "bjsxt"
      * (2) ("1" , "bjsxt")
      * 相同的key被后面的相同的key顶替掉，只保留一个
      */
    val map = Map[String, String]("1" -> "bjsxt", "2" -> "shsxt", ("3", "xasxt"))


    /**
      * 2、获取map的值
      *   map.get(“1”).get
      *
      * 如果map中没有对应项，赋值为getOrElse传的值:
      *   map.get(100).getOrElse(“no value”)：
      */
    // 获取值
    println(map.get("1")) // 返回的是Option类型，如果有该key则返回Some(bjsxt),否则返回None
    println(map.get("1").get) // 直接返回value，如果没有该key则报错:None.get
    val result = map.get("8").getOrElse("no value")
    println(result)

    /**
      * 3、遍历map
      * （1）遍历每个 map元素: for, foreach
      * （2）遍历 key: map.keys
      * （3）遍历 value: map.values
      *
      */
    // map遍历k-v
    for (x <- map) {
      println("====key:" + x._1 + ",value:" + x._2)
    }
    map.foreach(f => {
      println("key:" + f._1 + " ,value:" + f._2)
    })

    // 遍历key: map.keys
    val keyIterable = map.keys
    keyIterable.foreach(key => {
      println("key:" + key)
    })
    println("---------")

    // 遍历value: map.values
    val valueIterator = map.values
    valueIterator.foreach(value => {
      println("valus:" + value)
    })


    /**
      * 4、合并map
      * 合并时会将map中相同key的value替换
      * ++ 例：map1.++(map2)  --返回一个map，将map2加到map1中
      * ++: 例：map1.++:(map2) --返回一个map，将map1加到map2中
      */
    val map1 = Map[String, Int](("a", 1), ("b", 2), ("c", 3))
    val map2 = Map[String, Int](("a", 4), ("b", 5))
    map1.++(map2).foreach(println) // map2加到map1里，("a",4),("b",5),("c",3)
    map1.++:(map2).foreach(println) // map1加到map2里，("a",1),("b",2),("c",3)

    /**
      * 5、可变map
      * 导包
      */
    // 可变map
    import scala.collection.mutable
    val map3 = mutable.Map[String, Int]()
    map3.put("a", 100)
    map3.put("b", 200)


    /**
      * 6、其他方法
      * filter：过滤，留下符合条件的记录
      * count：统计符合条件的记录数
      * contains：map中是否包含某个key
      * exist：符合条件的记录存在不存在
      *
      */
    // filter 方法，输出满足条件的
    val res: mutable.Map[String, Int] = map3.filter(tp => {
      val value = tp._2
      value == 200
    })
    res.foreach(println)

    println(map3.contains("a"))

    println(map3.exists(f => {
      f._2.equals(100)
    }))

  }
}
