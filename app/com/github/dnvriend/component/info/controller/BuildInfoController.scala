/*
 * Copyright 2016 Dennis Vriend
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.dnvriend.component.info.controller

import play.api.http.ContentTypes
import play.api.mvc.{ Action, Controller }
import org.slf4j.{ Logger, LoggerFactory }
import akka.actor._
import javax.inject._

class MyActor extends Actor {
  println("==> Actor created")

  override def receive = {
    case msg =>
      Thread.sleep(500)
      val helloWorld = s"Hello: $msg"
      println("===> " + helloWorld)
      sender() ! helloWorld
  }
}

class BuildInfoController @Inject() (@Named("myActor") ref: ActorRef) extends Controller {
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  def info = Action { request =>
    ref ! "Hello from request"
    log.debug(s"Received buildinfo from ${request.remoteAddress}")
    Ok(com.github.dnvriend.BuildInfo.toJson).as(ContentTypes.JSON)
  }
}

