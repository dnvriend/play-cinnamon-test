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

package com.github.dnvriend.component.health.controller

import javax.inject.Inject

import anorm._
import akka.pattern.CircuitBreaker
import com.github.dnvriend.component.health.util.DisjunctionOps._
import io.swagger.annotations._
import org.slf4j.{ Logger, LoggerFactory }
import play.api.db.Database
import play.api.mvc.{ Action, Controller }
import scala.concurrent._

import scalaz._

@Api(value = "/api/health")
class HealthController @Inject() (db: Database, cb: CircuitBreaker) extends Controller {
  val log: Logger = LoggerFactory.getLogger(this.getClass)

  @ApiOperation(value = "Endpoint for health check", response = classOf[String], httpMethod = "GET")
  @ApiResponses(Array(new ApiResponse(code = 200, message = "")))
  def check = Action { request =>
    log.debug(s"Received health from ${request.remoteAddress}")
    cb.withSyncCircuitBreaker(checkDatabase)
  }

  def checkDatabase: Disjunction[String, Long] = Disjunction.fromTryCatchNonFatal {
    db.withConnection { implicit conn =>
      SQL"SELECT 1".executeQuery().as(anorm.SqlParser.scalar[Long].single)
    }
  }.leftMap(_.toString)

  def checkWithFailure = Action.async(cb.withCircuitBreaker(Future.failed(new RuntimeException("This should fail!"))))
}

