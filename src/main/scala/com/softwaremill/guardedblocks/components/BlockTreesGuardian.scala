package com.softwaremill.guardedblocks.components

import scala.tools.nsc.Global

class BlockTreesGuardian[G <: Global](val global: G) {
  import global._

  val guard: Tree => Unit = BlockTreesGuardianTraverser.traverse

  private object BlockTreesGuardianTraverser extends Traverser {
    override def traverse(tree: Tree): Unit = {
      tree match {
        case block@Block(stats, expr) =>
          for {
            stat <- stats.filter(_.tpe <:< block.tpe)
          } global.reporter.error(stat.pos, s"Found unused statement!")
        case _ =>
      }
      super.traverse(tree)
    }
  }

}


