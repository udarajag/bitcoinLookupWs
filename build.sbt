name := "bitcoinLookupWs"
 
version := "1.0"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies ++= Seq(ws , specs2 % Test, guice )

libraryDependencies += "com.google.code.gson" % "gson" % "2.2.4"

libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.3"

libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3"

libraryDependencies += "com.h2database" % "h2" % "1.4.197"

libraryDependencies += specs2 % Test
      