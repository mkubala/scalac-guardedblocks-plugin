package com.softwaremill.guardedblocks.components

import scala.tools.nsc._
import scala.tools.nsc.plugins.PluginComponent

class GuardedBlocksComponent(val global: Global) extends PluginComponent {

  import global._

  override val phaseName: String = "guardblocks"
  override def description: String = "check for unused statements in code blocks"
  override val runsAfter: List[String] = List("typer")

  private val guardian = new BlockTreesGuardian[global.type](global)

  override def newPhase(prev: Phase): Phase = new StdPhase(prev) {
    override def apply(unit: CompilationUnit): Unit = guardian.guard(unit.body)
  }

}
