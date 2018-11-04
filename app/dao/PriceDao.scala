package dao

import java.sql.Date
import javax.inject.{Inject, Singleton}

import models.Price
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}


case class StoredPrice(id: Long, price: Double, time: Date)

/**
  * A repository for prices.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class PriceDao @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of prices
    */
  class PriceTable(tag: Tag) extends Table[StoredPrice](tag, "prices") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def price = column[Double]("price")

    def time = column[Date]("time")

    def * = (id, price, time) <> ((StoredPrice.apply _).tupled, StoredPrice.unapply)
  }

  private val prices = TableQuery[PriceTable]


  def create(price: Price): Future[(Long, Double, Date)] = db.run {
    (prices.map(p => (p.price, p.time))
      returning prices.map(_.id)
      into ((nameAge, id) => (id, nameAge._1, nameAge._2))
      ) += (price.price, new Date(price.time.getTime))
  }

  def getPriceBetweenDate(from: Date, to: Date) = {
    val priceByDate = prices.filter(_.time >= from).filter(_.time <= to)
    db.run(priceByDate.result)
  }

  def getTodaysPrice(date: Date) = {
    db.run(prices.filter(_.time === date).result)
  }

  def delete = db.run {
    prices.delete
  }

  def list(): Future[Seq[StoredPrice]] = db.run {
    prices.result
  }
}
