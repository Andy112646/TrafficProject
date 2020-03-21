package com.bjsxt.data

import java.io.{FileOutputStream, PrintWriter}
import java.lang.Math._

class DataGenerator {

}

/**
  * 默认应该是十万条记录  一万个用户   一千条记录  相应的作者应该是两百个,这里按照比例模拟，比例确定如果有10000条记录，有一千个用户，100个app
  * 模拟：
  * 应用词表					applist.txt
  * 用户历史下载表			userdownload.txt
  * 正负例样本表			sample.txt
  * 每张表模拟10000 条数据 ，分别存入三个文件
  */
object DataGenerator {

  def main(args: Array[String]) {
    //从业务表里面抽取出来的描述应用的基本特征
    makelist(args(0).toInt, args(1)) // 应用词表,
    makeappdown(args(0).toInt, args(1)) // 用户历史下载 【如：主题,是一个应用,游戏中心,是一个应用,应用中心,是一个应用】
    makesample(args(0).toInt, args(1)) // 模拟正负例样本  = 浏览记录+lable
    println("all finished...")
  }

  /**
    *
    * 模拟app的商品词表
    * hitop_id    STRING,  	应用软件ID
    * name        STRING,  	名称
    * author      STRING,   作者
    * sversion    STRING,   版本号
    * ischarge    SMALLINT, 收费软件
    * designer    STRING,   设计者
    * font        STRING,   字体
    * icon_count  INT,      有几张配图
    * stars       DOUBLE,   评价星级
    * price       INT,      价格
    * file_size   INT,      大小
    * comment_num INT,      评论数据
    * screen      STRING,   分辨率
    * dlnum       INT       下载数量
    */
  //num 模拟条数 假设为10000，path ：文件写出的路径
  def makelist(num: Int, path: String): Unit = {
    val pw = new PrintWriter(new FileOutputStream(path + "/applist.txt", true))
    var str = new StringBuilder()
    for (i <- 1 to num) {
      str.clear()
      str.append("hitop_id").append((random() * num / 100).toInt).append("\t") //模拟100个软件ID
      str.append("name").append((random() * num / 100).toInt).append("\t") //模拟100个软件名称
      str.append("author").append((random() * num / 500).toInt).append("\t") //模拟20个作者
      str.append("sversion").append((10 * random()).toInt).append("\t") //模拟10个版本号
      str.append((2 * random()).toInt).append("\t") // 模拟是否收费 1：收费，0：不收费
      str.append("designer").append((random() * num / 500).toInt).append("\t") //模拟20个设计者
      str.append("font").append((20 * random()).toInt).append("\t") //模拟20个字体
      str.append((10 * random()).toInt).append("\t") //模拟每个软件最多10张配图
      //f"$xxx" scala中的插值   %1.2f保留两位小数
      str.append(f"${10 * random()}%1.2f").append("\t") //模拟星级
      str.append((random() * num).toInt).append("\t") //模拟价格
      str.append((random() * num).toInt).append("\t") //模拟大小
      str.append((random() * num / 50).toInt).append("\t") //模拟最多200个评论数据
      str.append("screen").append((random() * num / 20).toInt).append("\t") //模拟500个分辨率
      str.append((random() * num / 50).toInt) //模拟每个有最多200个下载量
      pw.write(str.toString());
      pw.println()
    }
    pw.flush()
    pw.close()
  }

  /**
    * 模拟每个用户下载的最多是10个app的列表
    */
  def makeapplist(num: Int): String = {
    var str = new StringBuilder()
    for (i <- 1 to (10 * random()).toInt + 1) {
      str.append("hitop_id").append((random() * num / 100).toInt).append(",")
    }
    //删除索引是最后的字符  --删除结尾的“,”
    str.deleteCharAt(str.length - 1)
    return str.toString()
  }

  /**
    * 模拟用户下载历史表   这里没有用户这个概念   手机设备ID就是userId
    * device_id           STRING,   	手机设备ID
    * devid_applist       STRING,     下载过软件列表
    * device_name         STRING,     设备名称
    * pay_ability         STRING      支付能力
    */
  def makeappdown(num: Int, path: String): Unit = {
    val pw = new PrintWriter(new FileOutputStream(path + "/userdownload.txt", true))
    var str = new StringBuilder()
    for (i <- 1 to num) {
      str.clear()
      str.append("device_id").append((random() * num / 10).toInt).append("\t") //模拟最多有1000个手机设备ID
      str.append(makeapplist(num)).append("\t") //模拟用户下载列表 最多10个
      str.append("device_name").append((random() * num / 10).toInt).append("\t") //模拟最多1000个设备名称
      str.append("pay_ability").append((4 * random()).toInt) //模拟4个支付等级
      pw.write(str.toString());
      pw.println()
    }
    pw.flush()
    pw.close()
  }

  /**
    * 正负例样本表 = 浏览记录+标签
    * label       			STRING,        	Y列，-1或1代表正负例    label值实际上是批处理得出来的，用户浏览了并在一段时间内下载为正例
    * device_id   			STRING,        	设备ID
    * hitop_id    			STRING,        	应用ID
    * screen      			STRING,        	手机软件需要的分辨率
    * en_name     			STRING,        	英文名
    * ch_name     			STRING,        	中文名
    * author      			STRING,        	作者
    * sversion    			STRING,        	版本
    * mnc         			STRING,				 	Mobile Network Code，移动网络号码
    * event_local_time 	STRING,					 当地时间
    * interface   			STRING,					下载/浏览的接口
    * designer    			STRING,					 软件设计者
    * is_safe     			INT,						是否安全
    * icon_count  			INT,						软件图片个数
    * update_time 			STRING,					事件更新时间
    * stars       			DOUBLE,					软件评级
    * comment_num 			INT,						软件评论数量
    * font        			STRING,					字体
    * price       			INT,						价格
    * file_size   			INT,						大小
    * ischarge    			SMALLINT,				是否免费
    * dlnum       			INT							下载数量
    */
  def makesample(num: Int, path: String): Unit = {
    val pw = new PrintWriter(new FileOutputStream(path + "/sample.txt", true))
    var str = new StringBuilder()
    for (i <- 1 to num) {
      str.clear()
      str.append(2 * (2 * random()).toInt - 1).append("\t") //浏览-1，下载1
      str.append("device_id").append((random() * num / 10).toInt).append("\t") //模拟1000个手机设备ID
      str.append("hitop_id").append((random() * num / 100).toInt).append("\t") //模拟100个软件ID
      str.append("screen").append((random() * 20).toInt).append("\t") //模拟500个分辨率
      str.append("en_name").append((random() * num / 100).toInt).append("\t") //模拟100个软件英文名
      str.append("ch_name").append((random() * num / 100).toInt).append("\t") //模拟100个软件中文名
      str.append("author").append((random() * num / 500).toInt).append("\t") //模拟20个作者
      str.append("sversion").append((10 * random()).toInt).append("\t") //模拟10个版本号
      str.append("mnc").append((random() * num / 10).toInt).append("\t") //模拟设备1000 手机网络信号
      str.append("event_local_time").append((random() * num).toInt).append("\t") //模拟10000个时间
      str.append("interface").append((random() * num).toInt).append("\t") //模拟10000个接口
      str.append("designer").append((random() * num / 500).toInt).append("\t") //模拟20个设计者
      str.append((2 * random()).toInt).append("\t") //模拟是否安全 0,1
      str.append((10 * random()).toInt).append("\t") //模拟最多10张匹配
      str.append("update_date").append((random() * num).toInt).append("\t") //模拟10000个更新时间
      str.append(f"${10 * random()}%1.2f").append("\t") //模拟软件评级
      str.append((random() * num / 50).toInt).append("\t") //模拟最多200个评论数量
      str.append("font").append((20 * random()).toInt).append("\t") //模拟字体
      str.append((random() * num).toInt).append("\t") //模拟价格
      str.append((random() * num).toInt).append("\t") //模拟大小
      str.append((2 * random()).toInt).append("\t") //是否免费
      str.append((random() * num / 50).toInt) //模拟下载数量 最多200个
      pw.write(str.toString());
      pw.println()
    }
    pw.flush()
    pw.close()
  }

}