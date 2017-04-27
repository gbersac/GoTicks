name := "goticks"

version := "0.0.1"
scalaVersion := "2.12.2"

organization := "com.goticks"

val akkaVersion = "2.5.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)
