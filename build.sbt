import sbt.Keys.libraryDependencies

name := "livy-client-test"

name := "livy-client"
organization := "com.zoyi.livy"
version := "1.0"
scalaVersion := "2.11.8"
retrieveManaged := true
//ivyScala := ivyScala.value map { _.copy(overrideScalaVersion = true) }

resolvers ++= Seq(
  "Cloudera repos" at "https://repository.cloudera.com/artifactory/cloudera-repos",
  "Cloudera releases" at "https://repository.cloudera.com/artifactory/libs-release",
  "central" at "http://central.maven.org/maven2/",
  Resolver.sonatypeRepo("public"),
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.mavenLocal
)

val livyClientVersion = "0.5.0-incubating"
val sparkVersion = "2.3.0"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.livy" % "livy-client-http" % livyClientVersion,
  "com.chuusai" %% "shapeless" % "2.3.3"
)


assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("META-INF", "native", "libnetty-transport-native-epoll.so") => MergeStrategy.first
  case PathList("io",    "netty", xs @ _*) => MergeStrategy.last
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".properties" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".xml" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".types" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith  "jersey-module-version" => MergeStrategy.first
  case "application.conf"                            => MergeStrategy.concat
  case "unwanted.txt"                                => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith "overview.html" => MergeStrategy.discard
  case PathList(ps @ _*) if ps.last endsWith "sun-jaxb.episode" => MergeStrategy.first
  case x =>
    val oldStrategy = (assemblyMergeStrategy in assembly).value
    oldStrategy(x)
}

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "wi-spark-assembly-1.0.jar"}
}