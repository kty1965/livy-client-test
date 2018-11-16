import shapeless._
import shapeless.syntax.std.traversable._
import shapeless.syntax.std.tuple._

/**
  * Created by huy on 2018-11-13.
  */
object ScalaCodeTest {
  def main(args: Array[String]): Unit = {
    val path = List("1_1F", "1_2F", "1_3F")
    val condition = path.dropRight(1)
        .zipWithIndex
        .map(x => path.slice(x._2, x._2 + 2) match { case List(from, to) => (from, to)})
    println(condition)
    println(condition.contains(("1_1F", "1_2F") ))
  }
}
