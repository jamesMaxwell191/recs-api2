package com.betgenius.controllers

import akka.NotUsed
import akka.http.scaladsl.model.HttpResponse
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.betgenius.model.UpdateGram
import com.betgenius.module.ActorModule

import scala.concurrent.Future
/**
  * Created by douglas on 21/02/16.
  */

trait UpdateGramControllerIf {
   def handle(updategram:UpdateGram)(implicit mat:ActorMaterializer):Future[HttpResponse]
}

class UpdateGramController(val flow:Flow[UpdateGram,HttpResponse,NotUsed]) extends UpdateGramControllerIf{

  def handle(updategram:UpdateGram)(implicit mat:ActorMaterializer):Future[HttpResponse] ={
    val theFlow = Source.single(updategram).via(flow)
    val result = theFlow.runWith(Sink.head[HttpResponse])
    result
  }

}

