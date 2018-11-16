import java.io.{File, FileNotFoundException}
import java.net.URI


import org.apache.livy._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.language.postfixOps
import util.control.Breaks._
import org.apache.spark.api.java.JavaSparkContext


/**
  * Created by huy on 2018-11-07.
  */

class SessionCountApp(val shopId: String,
                      val from: String,
                      val to: String,
                      val path: List[String])
    extends Job[String]{

  override def call(sc: JobContext): String = {
    val sparkConext: JavaSparkContext = sc.sc()
    val condition = path.dropRight(1)
      .zipWithIndex
      .map(x => path.slice(x._2, x._2 + 2) match { case List(from, to) => (from, to)})
  }
}

object SessionCountApp {

  var scalaClient: LivyClient = null

  /**
    *  Initializes the Scala client with the given url.
    *  @param url The Livy server url.
    */
  def init(url: String): Unit = {
    scalaClient = new LivyClientBuilder(false)
        .setURI(new URI(url))
        .setConf("spark.dynamicAllocation.minExecutors", "16")
        .build()
  }

  /**
    *  Uploads the Scala-API Jar and the examples Jar from the target directory.
    *  @throws FileNotFoundException If either of Scala-API Jar or examples Jar is not found.
    */
  @throws(classOf[FileNotFoundException])
  def uploadRelevantJarsForJobExecution(): Unit = {
    val exampleAppJarPath = getSourcePath(this)
    val scalaApiJarPath = getSourcePath(scalaClient)
    uploadJar(exampleAppJarPath)
    uploadJar(scalaApiJarPath)
  }

  @throws(classOf[FileNotFoundException])
  private def getSourcePath(obj: Object): String = {

    val source = obj.getClass.getProtectionDomain.getCodeSource
    if (source != null && source.getLocation.getPath != "") {
      source.getLocation.getPath
    } else {
      throw new FileNotFoundException(s"Jar containing ${obj.getClass.getName} not found.")
    }
  }

  private def uploadJar(path: String) = {
    val file = new File(path)
    val uploadJarFuture = scalaClient.uploadJar(file)
  }

  /**
    * Submits a spark sql job to the livy server.
    *
    * The sql context job reads data frames from the given json path and executes
    * a sql query to get the word with max count on the temp table created with data frames.
    * @param inputPath Input path to the json data containing the words.
    */
  def getSessionCount(): String = {
    scalaClient.submit(new SessionCountApp("1", "2018-06-01", "2018-10-31", List("1", "2", "3"))).get()
  }

  private def stopClient(): Unit = {
    if (scalaClient != null) {
      scalaClient.stop(true)
      scalaClient = null
    }
  }

  def main(args: Array[String]): Unit = {
    var url = ""
    require(args.length >= 1 && args.length <= 1)
    url = args(0)
    try {
      val f = Future {
        init(url)
      }
      println("created" + Await.result(f, 60 second))

      val f2 = Future {
        getSessionCount()
      }
      println("getSessionCount(): \n" + Await.result(f2, 600 second))

    } finally {
      stopClient()
    }
  }
}