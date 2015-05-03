package com.softwaremill.guardedblocks

import java.io.File
import java.net.URLClassLoader

import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}

import scala.reflect.internal.util.{BatchSourceFile, SourceFile}
import scala.reflect.io.PlainFile
import scala.tools.nsc._
import scala.tools.nsc.io.VirtualDirectory
import scala.tools.nsc.plugins.Plugin
import scala.tools.nsc.reporters.StoreReporter
import scala.tools.nsc.util.ClassPath

class GuardedBlocksPluginSpec extends WordSpec with Matchers with BeforeAndAfterEach {

  sealed trait CompileTestResult { def label: String }
  case object Success extends CompileTestResult { val label = "compile without error" }
  case object Failure extends CompileTestResult { val label = "cause compile errors" }

  type CompileTestCase = (File, CompileTestResult)
  type CompilationErrorsCount = Int

  lazy val reporter = new StoreReporter
  
  override protected def beforeEach(): Unit = {
    reporter.reset()
    super.beforeEach()
  }

  val compiler = {
    val settings: Settings = {
      def getSbtCompatibleClasspath: String = {
        val loader = getClass.getClassLoader.asInstanceOf[URLClassLoader]
        val entries = loader.getURLs map(_.getPath)
        val sclpath = entries find(_.endsWith("scala-compiler.jar")) map(
          _.replaceAll("scala-compiler.jar", "scala-library.jar"))
        ClassPath.join(entries ++ sclpath: _*)
      }

      val s = new Settings
      s.outputDirs.setSingleOutput(new VirtualDirectory("<memory>", None))
      s.classpath.value = getSbtCompatibleClasspath
      s
    }
    
    new Global(settings, reporter) {
      override protected def loadRoughPluginsList(): List[Plugin] =
        new GuardedBlocksPlugin(this) :: super.loadRoughPluginsList()
    }
  }

  def testCasesFrom(resourcePath: String) = {
    val testCasesDir = new File(getClass.getResource(resourcePath).toURI)
    testCasesDir.listFiles().collect {
      case f: File if f.getName.endsWith("Fail.scala") =>
        (f, Failure)
      case f: File if f.getName.endsWith("Success.scala") =>
        (f, Success)
    }
  }

  def runCompilation(source: SourceFile): CompileTestResult = {
    new compiler.Run().compileSources(List(source))
    if (reporter.errorCount == 0) Success
    else Failure
  }

  "Blocks guardian" should {
    testCasesFrom("/testCases").foreach { testCase =>
      val (sourceFile, expectedResult) = testCase
      s"${expectedResult.label} when compiling ${sourceFile.getName}" in {
        val file = new PlainFile(sourceFile.getAbsolutePath)
        runCompilation(new BatchSourceFile(file)) shouldBe expectedResult
      }
    }
  }

}
