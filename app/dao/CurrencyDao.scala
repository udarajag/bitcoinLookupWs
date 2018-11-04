package dao

import java.sql.Date
import javax.inject.Inject

import models.{Data, Price}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

case class CurrencyData(id: Long, base: String, currency: String)

class CurrencyDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class CurrencyTable(tag: Tag) extends Table[CurrencyData](tag, "Currencies") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def base = column[String]("base")

    def currency = column[String]("currency")

    def * = (id, base, currency) <> ((CurrencyData.apply _).tupled, CurrencyData.unapply)
  }

  private val currency = TableQuery[CurrencyTable]

  def delete() = {
    db.run(currency.delete)
  }

  def create(data: Data): Future[(Long, String, String)] = db.run {
    (currency.map(d => (d.base, d.currency))
      returning currency.map(_.id)
      into ((nameAge, id) => (id, nameAge._1, nameAge._2))
      ) += (data.base, data.currency)
  }

  def get(): Future[CurrencyData] = {
    val head = currency.result.head
    db.run(head)
  }
}
