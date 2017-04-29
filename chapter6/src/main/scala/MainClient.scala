import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem

object MainClient extends App {
  val config = ConfigFactory.load("client")
  val system = ActorSystem("client", config)
}
