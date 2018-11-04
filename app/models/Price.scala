package models

import java.util.Date

import play.api.libs.json.Json

case class Price(id:Long, price: Double, time: Date) extends Serializable

object Price {
  implicit val personFormat = Json.format[Price]
}
