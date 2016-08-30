name := """play-java"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

lazy val scalaz = "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.0",
  "edu.stanford.nlp" % "stanford-corenlp" % "3.5.0" classifier "models",
  "uk.ac.gate" % "gate-core" % "8.1",
  "org.javassist" % "javassist" % "3.18.2-GA"
)


fork in run := true
