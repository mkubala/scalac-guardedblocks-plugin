package com.softwaremill.guardedblocks.components

import scala.tools.nsc._
import scala.tools.nsc.ast.Printers
import scala.tools.nsc.plugins.PluginComponent
import scala.tools.nsc.transform._

class GuardedBlocksComponent(val global: Global) extends PluginComponent with Transform with TypingTransformers {

  import global._

  override val phaseName: String = "guardblocks"

  override def description: String = "check for unused statements in code blocks"

  override val runsAfter: List[String] = List("typer")

  override protected def newTransformer(unit: CompilationUnit): Transformer =
    new GuardedBlockTransformer(unit)

  class GuardedBlockTransformer(unit: CompilationUnit) extends TypingTransformer(unit) {
    override def transform(tree: Tree): Tree = {
      tree match {
        case block@Block(stats, expr) =>
          for {
            stat <- stats.filter(_.tpe <:< block.tpe)
          } global.reporter.error(stat.pos, s"Found unused statement!")
        case _ =>
      }
      super.transform(tree)
    }

  }

}
