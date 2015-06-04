package akkaHttp.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.{ActorFlowMaterializer, FlowMaterializer}

import scala.concurrent.ExecutionContextExecutor

class MethodArgFail {
  implicit val system: ActorSystem = ActorSystem()

  implicit def executor: ExecutionContextExecutor = system.dispatcher

  implicit val materializer: FlowMaterializer = ActorFlowMaterializer()

  val routes = {
    def prefixed(route: Route): Route = path("somePrefix") {
      route
    }

    prefixed {
      get {
        complete(OK -> "Completed")
      }
      post {
        complete(OK -> "Completed")
      }
    }

  }

}
