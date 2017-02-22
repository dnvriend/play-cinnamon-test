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

package com.github.dnvriend.component.health.util

import play.api.libs.json.{ Format, Json }
import play.api.mvc.{ Result, Results }

import scala.language.implicitConversions
import scalaz._

object DisjunctionOps extends Results {
  def tryCatch[A](block: => A): Disjunction[String, A] =
    Disjunction.fromTryCatchNonFatal(block).leftMap(_.toString)

  implicit def ToActionUnit(maybe: Disjunction[String, Unit]): Result =
    maybe.map(value => NoContent)
      .leftMap(messages => BadRequest(messages)) match {
        case DRight(result) => result
        case DLeft(result)  => result
      }

  implicit def ToActionMaybe[A: Format](maybe: Disjunction[String, Option[A]]): Result =
    maybe
      .map(maybeValue => maybeValue.map(value => Ok(Json.toJson(value))).getOrElse(NotFound))
      .leftMap(messages => BadRequest(messages)) match {
        case DRight(result) => result
        case DLeft(result)  => result
      }

  implicit def ToActionList[A: Format](maybe: Disjunction[String, List[A]]): Result =
    maybe.map(xs => Ok(Json.toJson(xs)))
      .leftMap(messages => BadRequest(messages)) match {
        case DRight(result) => result
        case DLeft(result)  => result
      }

  implicit def ToActionAny[A: Format](maybe: Disjunction[String, A]): Result =
    maybe.map(value => Ok(Json.toJson(value)))
      .leftMap(messages => BadRequest(messages)) match {
        case DRight(result) => result
        case DLeft(result)  => result
      }
}

