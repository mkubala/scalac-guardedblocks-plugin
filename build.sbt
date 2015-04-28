name := "scalac-guardedblocks-plugin"

organization := "com.softwaremill.guardedblocks"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
	"org.scala-lang" % "scala-compiler" % scalaVersion.value,
  "org.scalatest" % "scalatest_2.11" % "2.2.1" % "test"
)

