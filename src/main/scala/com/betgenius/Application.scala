package com.betgenius

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.http.scaladsl.server.{RouteResult, Route, ExceptionHandler}
import akka.pattern.ask
import akka.routing.RoundRobinPool
import akka.util.Timeout
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.Uri.Path.Segment
import akka.stream.ActorMaterializer
import com.betgenius.repository.EntityManager
import scala.concurrent.Future
import scala.concurrent.duration._
import akka.http.scaladsl.marshallers.xml.ScalaXmlSupport._
import akka.http.scaladsl.server.Directives._


import scala.concurrent.ExecutionContext.Implicits.global

/**
  * This spray applicaton exposes a rest web service which provides
  * program recommendations for a sky subscriber
  * The results returned by the service are configurable in application.conf as is
  * the service itself and the remote rest application providing the actual recommendations
  *
  * The results returned by the service are cached. The way caching is currently done
  * means that if a request is made and the remote server is down the error result will
  * go in the cache.
  * If the remote server comes back up it will not be accessible for an user in the cache
  * until the cached values expires.
  */
object Application extends App with BetGenius {

  implicit val actorSystem = ActorSystem("recs")

  implicit val materializer = ActorMaterializer()

  implicit val timeout = Timeout(60 seconds)

  implicit val fixtureUnmarshaller = defaultNodeSeqUnmarshaller.map(toFixture(_))

  val (host,port) = ("localhost",9142)

  lazy val echoActor = actorSystem.actorOf(RoundRobinPool(5).props(EchoActor.props), "echoRouter")

  lazy val entityActor = actorSystem.actorOf(RoundRobinPool(5).props(EntityManager.props), "entityRouter")


  val handler = Http().bindAndHandle(RouteResult.route2HandlerFlow(fixtureDataRoute),interface = host,port = port)

  handler onFailure {
    case e:Exception => println(s"failed to bind $host $port")
  }

}
