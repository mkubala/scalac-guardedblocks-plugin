package com.softwaremill.guardedblocks.components

import scala.tools.nsc.Global

class BlockTreesGuardian[G <: Global](val global: G) {

  import global._

  val guard: Tree => Unit = BlockTreesGuardianTraverser.traverse

  private object BlockTreesGuardianTraverser extends Traverser {
    override def traverse(tree: Tree): Unit = {
      if (isTypedStatement(tree) && !hasUnitTpe(tree)) {
        tree match {
          case block@Block(statements, expr) =>
            for {
              statement <- statements.filter(isTypedStatement).filter(resolveType(_) <:< block.tpe)
            } global.reporter.error(statement.pos, s"Found unused statement!")
          case _ =>
        }
      }
      super.traverse(tree)
    }

    def isTypedStatement(tree: Tree): Boolean =
      tree.tpe != null

    def hasUnitTpe(tree: Tree): Boolean =
      tree.tpe.dealias == typeOf[Unit]

    def resolveType(tree: Tree): Type = tree match {
      case ifTree@If(cond, thenBlock, elseBlock) if hasUnitTpe(elseBlock) && thenBlock.tpe.dealias != elseBlock.tpe.dealias =>
        thenBlock.tpe
      case oth => oth.tpe
    }

  }

}


