import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._

object RestApi {

  def routes: Route = eventsRoute ~ eventRoute ~ ticketsRoute

  def eventsRoute =
    pathPrefix("events") {
      pathEndOrSingleSlash {
        get {
          /**
           * Get all events
           * route : GET /events
           * request body : N/A
           * response : [ { event : "RHCP", tickets : 249 }, { event : "Radiohead", tickets : 130 } ]
           */
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "GET /events"))
        }
      }
    }

  def eventRoute =
    pathPrefix("events" / Segment) { event =>
      pathEndOrSingleSlash {
        post {
          /**
           * Create an event
           * route : POST /events/:event
           * request body : { "tickets" : 250}
           * response : { "name": "RHCP", "tickets": 250 }
           */
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "POST /events/:event"))
        } ~
        get {
          /**
           * Return one event
           * route : GET /events/:event
           * request body : N/A
           * response : { event : "RHCP", tickets : 249 }
           */
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "GET /events/:event"))
        } ~
        delete {
          /**
           * Cancel an event
           * route : DELETE /events/:event
           * request body : N/A
           * response : { event : "RHCP", tickets : 249 }
           */
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "DELETE /events/:event"))
        }
      }
    }

  def ticketsRoute =
    pathPrefix("events" / Segment / "tickets") { event =>
      post {
        pathEndOrSingleSlash {
          /**
           * Buy tickets
           * route : POST /events/:event/tickets
           * request body : { "tickets" : 2 }
           * response : { "event" : "RHCP", "entries" : [ { "id" :1},{"id":2}] }
           */
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "POST /events/:event/tickets"))
        }
      }
    }

}
