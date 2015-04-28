package com.softwaremill.guardedblocks

import java.net.URLClassLoader

import org.scalatest.{Matchers, FlatSpec}

import scala.tools.nsc._
import scala.tools.nsc.io.VirtualDirectory
import scala.reflect.internal.util.BatchSourceFile
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.reporters.{StoreReporter, ConsoleReporter}
import scala.tools.nsc.util.ClassPath

class GuardedBlocksPluginSpec extends FlatSpec with Matchers {

  val code =
    """object Hello extends App {
      |  println("Hello World!")
      |}""".stripMargin

  val sources = List(new BatchSourceFile("<test>", code))

  val settings: Settings = {
    def getSbtComaptibleClasspath: String = {
      val loader = getClass.getClassLoader.asInstanceOf[URLClassLoader]
      val entries = loader.getURLs map(_.getPath)
      val sclpath = entries find(_.endsWith("scala-compiler.jar")) map(
        _.replaceAll("scala-compiler.jar", "scala-library.jar"))
      ClassPath.join(entries ++ sclpath: _*)
    }

    val s = new Settings
    s.outputDirs.setSingleOutput(new VirtualDirectory("<memory>", None))
    s.classpath.value = getSbtComaptibleClasspath
    s
  }

  val reporter = new StoreReporter

  val compiler = new Global(settings, reporter) {
    override protected def loadRoughPluginsList(): List[Plugin] =
      new GuardedBlocksPlugin(this) :: super.loadRoughPluginsList()
  }

  it should "compile code with no errors" in {
    new compiler.Run().compileSources(sources)
    reporter.errorCount should be(0)
  }

}
