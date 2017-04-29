name := "goticks"

version := "0.0.1"
scalaVersion := "2.12.2"

organization := "com.goticks"

val akkaVersion = "2.4.17"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % "10.0.5",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5",
  "com.typesafe.akka" %% "akka-slf4j"      % akkaVersion,
  "ch.qos.logback"    %  "logback-classic" % "1.1.3",
  "io.spray" %%  "spray-json" % "1.3.3",

  "com.typesafe.akka" %% "akka-testkit"    % akkaVersion   % "test"
)
