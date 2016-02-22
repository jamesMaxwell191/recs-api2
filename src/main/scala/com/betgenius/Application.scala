package com.betgenius

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.RouteResult
import akka.stream.ActorMaterializer
import com.betgenius.controllers.UpdateGramController
import com.betgenius.module.{ActorModule}
import com.softwaremill.macwire._
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
object Application extends App with ActorModule with BetGenius {

  val (host,port) = ("localhost",9142)

  implicit val actorSystem = ActorSystem("BetGenius")

  implicit val materializer = ActorMaterializer()

  val datagramHandler = wire[UpdateGramController]


  val handler = Http().bindAndHandle(RouteResult.route2HandlerFlow(fixtureDataRoute),interface = host,port = port)

  handler onFailure {
    case e:Exception => println(s"failed to bind $host $port")
  }

}
