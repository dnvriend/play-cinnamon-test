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

package com.github.dnvriend.component.actor

import javax.inject.Inject

import akka.actor.{ Actor, ActorRef, ActorSystem, Stash }
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.name.Named
import com.lightbend.cinnamon.akka.TracerExtension
import com.typesafe.conductr.bundlelib.play.api.{ LocationService, StatusService }
import com.typesafe.conductr.bundlelib.scala.{ CacheLike, Env }
import com.typesafe.conductr.lib.play.api.ConnectionContext
import org.slf4j.{ Logger, LoggerFactory }
import play.api.mvc.{ Action, Controller }

import scala.concurrent.{ ExecutionContext, Future }

// see: https://github.com/typesafehub/conductr-lib#play25-conductr-bundle-lib
class ActorController @Inject() (system: ActorSystem, ctx: ConnectionContext, env: Env, locationService: LocationService, statusService: StatusService, locationCache: CacheLike, @Named("fooActor") fooActor: ActorRef)(implicit ec: ExecutionContext, timeout: Timeout) extends Controller {
  val log: Logger = LoggerFactory.getLogger(this.getClass)
  println(
    s"""
       |Env:
       |=====
       |  isRunByConductR: ${env.isRunByConductR}
       |  bundleId: ${env.bundleId}
       |  conductR status: ${env.conductRStatus}
     """.stripMargin
  )

  // ConnectionContext: When performing Play.WS connections, this is the connection context to use.
  // Env: Standard ConductR environment vars.
  // StatusService: used to communicate the bundle status to the Typesafe ConductR Status Server. Note this is handled automatically for you

  def askFoo(): Future[String] = (fooActor ? "Hello").mapTo[String]
  def action = Action.async(askFoo().map(resp => Ok(resp)))
}

class FooActor @Inject() (tracer: TracerExtension, @Named("barActor") barActor: ActorRef) extends Actor with Stash {
  override def receive: Receive = {
    case msg =>
      tracer.start("foo-request") {
        barActor ! "Hello"
        context.become(waitingForResponse(msg, sender()))
      }
  }

  def waitingForResponse(theMsg: Any, respondTo: ActorRef): Receive = {
    case msg if sender() == barActor =>
      context.become(receive)
      tracer.end("bar-response")
      respondTo ! s"$theMsg $msg"
      unstashAll

    case msg => stash()
  }

}

class BarActor @Inject() (tracer: TracerExtension) extends Actor {
  override def receive: Receive = {
    case _ =>
      tracer.end("foo-request")
      tracer.start("bar-response") {
        sender() ! "World!!"
      }
  }
}
