package com.betgenius

import akka.actor.{ActorLogging, Props, Actor}
import com.betgenius.EchoActor.EchoMessage
import org.joda.time.DateTime

/**
  * Created by douglas on 06/02/16.
  */
class EchoActor extends Actor with ActorLogging{

     override def receive = {
       case EchoMessage(text) => log.info("processing an echo message")
                        Thread.sleep(1500)
                        sender ! s"hello there $text"
     }

}

object EchoActor {
     def props = Props[EchoActor]

     case class EchoMessage(text:String)
}
