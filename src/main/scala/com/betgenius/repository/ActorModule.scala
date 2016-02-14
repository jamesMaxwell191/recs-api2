package com.betgenius.repository

import akka.actor.ActorSystem
import akka.routing.RoundRobinPool
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.betgenius.EchoActor
import scala.concurrent.duration._

/**
  * Created by douglas on 13/02/16.
  */
trait ActorModule {

  implicit val actorSystem = ActorSystem("recs")

  implicit val materializer = ActorMaterializer()

  lazy val echoActor = actorSystem.actorOf(RoundRobinPool(20).props(EchoActor.props).withDispatcher("echo-dispatcher"), "echoRouter")

  lazy val entityActor = actorSystem.actorOf(RoundRobinPool(5).props(EntityManager.props), "entityRouter")

}
