import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.pattern.ask

import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.actor.ActorRef
import akka.stream.ActorMaterializer
import akka.util.Timeout

class RestApi(
  boxOfficeActor: ActorRef
)(implicit
  materializer: ActorMaterializer,
  ec: ExecutionContext,
  timeout: Timeout
) extends EventMarshalling {
  import StatusCodes._
  import BoxOffice._
  import TicketSeller._

  def createEvent(event: String, nrOfTickets: Int) =
    boxOfficeActor.ask(CreateEvent(event, nrOfTickets)).mapTo[EventResponse]

  def getEvents() =
    boxOfficeActor.ask(GetEvents).mapTo[Events]

  def getEvent(event: String) =
    boxOfficeActor.ask(BoxOffice.GetEvent(event)).mapTo[Option[Event]]

  def cancelEvent(event: String) =
    boxOfficeActor.ask(CancelEvent(event)).mapTo[Option[Event]]

  def requestTickets(event: String, tickets: Int) =
    boxOfficeActor.ask(GetTickets(event, tickets)).mapTo[TicketSeller.Tickets]

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
          onSuccess(getEvents()) { events =>
            complete(OK, events)
          }
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
          entity(as[EventDescription]) { ed =>
            onSuccess(createEvent(event, ed.tickets)) {
              case BoxOffice.EventCreated(event) => complete(Created, event)
              case BoxOffice.EventExists =>
                val err = Error(s"$event event exists already.")
                complete(BadRequest, err)
            }
          }
        } ~
        get {
          /**
           * Return one event
           * route : GET /events/:event
           * request body : N/A
           * response : { event : "RHCP", tickets : 249 }
           */
          onSuccess(getEvent(event)) {
            _.fold(complete(NotFound))(e => complete(OK, e))
          }
        } ~
        delete {
          /**
           * Cancel an event
           * route : DELETE /events/:event
           * request body : N/A
           * response : { event : "RHCP", tickets : 249 }
           */
          onSuccess(cancelEvent(event)) {
            _.fold(complete(NotFound))(e => complete(OK, e))
          }
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
          entity(as[TicketRequest]) { request =>
            onSuccess(requestTickets(event, request.tickets)) { tickets =>
              if(tickets.entries.isEmpty) complete(NotFound)
              else complete(Created, tickets)
            }
          }
        }
      }
    }

}
