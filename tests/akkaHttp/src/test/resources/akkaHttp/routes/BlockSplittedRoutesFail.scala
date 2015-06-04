package akkaHttp.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}

import scala.concurrent.ExecutionContextExecutor

class BlockSplittedRoutesFail {
  implicit val system: ActorSystem = ActorSystem()

  implicit def executor: ExecutionContextExecutor = system.dispatcher

  implicit val materializer: FlowMaterializer = ActorFlowMaterializer()

  val routes = {
    {
      get {
        complete(OK)
      }
      println("I love dumplings!")
      post {
        complete(OK)
      }
    }
  }

}
