import sbt.Keys._

organization in ThisBuild := "com.softwaremill.guardedblocks"

scalaVersion in ThisBuild := "2.11.6"

version in ThisBuild := "0.1-SNAPSHOT"

lazy val commonSettings = Seq.empty[Setting[_]]

lazy val commonTestsSettings = commonSettings ++ Seq(
  libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "2.2.4")
)

lazy val root = (project in file("."))
  .aggregate(scalacPlugin, tests)
  .settings(name := "scalac-guardedblocks-plugin")
  .settings(commonSettings: _*)

lazy val scalacPlugin = (project in file("scalacPlugin"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-compiler" % scalaVersion.value
  ))

lazy val tests = (project in file("tests"))
  .aggregate(akkaHttpTests, basicTests)

lazy val commonTests = (project in file("tests/common"))
  .dependsOn(scalacPlugin)
  .settings(commonTestsSettings: _*)

lazy val basicTests = (project in file("tests/basic"))
  .dependsOn(commonTests)


lazy val akkaHttpTests = (project in file("tests/akkaHttp"))
  .dependsOn(commonTests)
  .settings(libraryDependencies ++= akkaHttpDeps)

lazy val akkaHttpDeps = {
  val akkaStreamV = "1.0-RC2"
  Seq(
    "com.typesafe.akka" %% "akka-stream-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-scala-experimental" % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaStreamV
  ).map(_ % "test")
}
