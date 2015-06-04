package akkaHttp.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}

import scala.concurrent.ExecutionContextExecutor

class MethodDefinitionSuccess {
  implicit val system: ActorSystem = ActorSystem()

  implicit def executor: ExecutionContextExecutor = system.dispatcher

  implicit val materializer: FlowMaterializer = ActorFlowMaterializer()

  val routes = {
    def buildRoute = {
      get {
        complete(OK)
      } ~
        post {
          complete(OK)
        }
    }

    buildRoute
  }

}
