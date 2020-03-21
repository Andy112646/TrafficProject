package com.wjx.scala

/**
  * @Auther:wjx
  * @Date:2019 /7/16
  * @Description:com.wjx.scala
  * @version:1.0
  */

/**
  * 查看scala的源码发现其String引用的是Java的包java.lang.String
  * 所基本上是和java一样的（如有不同则可能重写了）
  */
object Lesson_String {
  def main(args: Array[String]): Unit = {
    val str = "abcd"
    val str1 = "ABCD"
    println(str.indexOf(97))
    println(str.indexOf("b"))
    println(str == str1)
    println(str.compareToIgnoreCase(str1))

    val stringBuilder = new StringBuilder
    stringBuilder.append("abc")

    // + 接char
    stringBuilder.+('d')
    stringBuilder + 'd'

    // += 接char
    stringBuilder.+=('e')
    stringBuilder += 'e'

    // ++= 接字符串
    stringBuilder.++=("fgh")
    stringBuilder ++= "fgh"

    stringBuilder.append(1.0)
    stringBuilder.append(18f)

    println(stringBuilder)
  }

}
