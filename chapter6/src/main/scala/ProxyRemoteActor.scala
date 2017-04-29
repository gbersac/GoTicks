import scala.concurrent.Future

import akka.actor._
import akka.util.Timeout

class ProxyRemoteActor(implicit timeout: Timeout) extends Actor {
  import ProxyRemoteActor._

  def receive = {
    case TransferMessage(msg) =>
      println(msg)
  }

}

object ProxyRemoteActor {
  def props(implicit timeout: Timeout) = Props(new ProxyRemoteActor)

  sealed trait Input
  case class TransferMessage(message: Any) extends Input
}
