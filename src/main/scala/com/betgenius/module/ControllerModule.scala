package com.betgenius.module

import com.betgenius.controllers.UpdateGramController
import com.softwaremill.macwire._

/**
  * Created by douglas on 21/02/16.
  */
trait ControllerModule extends ActorModule {

     val datagramHandler = wire[UpdateGramController]

}