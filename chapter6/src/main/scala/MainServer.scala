import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.pattern._
import akka.util.Timeout
import akka.http.scaladsl.server._

import org.joda.time.DateTime

object MainServer extends App {
  implicit val system = ActorSystem()
  implicit val ec = system.dispatcher
  implicit val materializer = ActorMaterializer()
  implicit val timeout: Timeout = FiniteDuration(1, "s")

  val proxyActor = system.actorOf(ProxyRemoteActor.props, "proxy")

  val route =
    pathPrefix("printRemote") {
      pathEndOrSingleSlash {
        get {
          proxyActor ! ProxyRemoteActor.TransferMessage("print on remote " + DateTime.now.toString)
          complete(StatusCodes.OK)
        }
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 4242)
  scala.io.StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done

}
