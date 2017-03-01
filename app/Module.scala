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

import akka.actor.ActorSystem
import akka.util.Timeout
import com.github.dnvriend.component.actor.{ BarActor, FooActor }
import com.github.dnvriend.component.info.controller.MyActor
import com.google.inject.{ AbstractModule, Provides }
import com.lightbend.cinnamon.akka.{ Tracer, TracerExtension }
import play.api.libs.concurrent.AkkaGuiceSupport

import scala.concurrent.duration._

class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[MyActor]("myActor")
    bindActor[BarActor]("barActor")
    bindActor[FooActor]("fooActor")

    bind(classOf[Timeout])
      .toInstance(10.seconds)
  }

  @Provides
  def tracer(system: ActorSystem): TracerExtension = {
    Tracer(system)
  }
}
