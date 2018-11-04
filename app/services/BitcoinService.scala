package services

import java.sql.Date
import java.util
import javax.inject.Inject

import com.google.gson.{Gson, GsonBuilder, JsonParser}
import dao.{CurrencyDao, CurrencyData, PriceDao, StoredPrice}
import models.{BitCoinContainer, Data, Price}
import org.joda.time.DateTime
import play.api.libs.ws._

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success, Try}

/**
  * @param ws
  */
class BitcoinService @Inject()(ws: WSClient
                               , priceDao: PriceDao
                               , currencyDao: CurrencyDao
                               , gson: Gson, parser: JsonParser) {


  private def paersePriceJson(priceF: Future[Seq[StoredPrice]]) = {
    val gsonP = new GsonBuilder().setPrettyPrinting.create
    Try(Await.result(priceF, Duration.Inf)) match {
      case Success(results) =>
        val prices = results.toArray.map(p => Price(p.id, p.price, p.time)).sortBy(_.time).reverse
        val rollingAvg = if (prices.length == 0)
          0.00
        else
          "%.2f".format(prices.map(p => p.price).foldLeft(0.0)(_ + _) / prices.length).toDouble
        val data = getBase() match {
          case Some(base) =>
            Data(base.base, base.currency, rollingAvg, prices)
          case None => Data("", "", rollingAvg, prices)
        }
        gsonP.toJson(data)
      case Failure(fail) => s"Error ${fail.getMessage}"
    }
  }

  private def sqlDate(dataTime: DateTime): Date = {
    new Date(dataTime.toDateTime.getMillis)
  }

  /**
    *
    * @return
    */
  def todaysPrice: String = {
    paersePriceJson(priceDao.getTodaysPrice(sqlDate(new DateTime())))
  }

  /**
    *
    * @return The base currency details from the currency repository CurrencyDao
    */
  def getBase(): Option[CurrencyData] = {
    Try(Await.result(currencyDao.get, Duration.Inf)) match {
      case Success(results) =>
        Some(results)
      case Failure(fail) => None
    }
  }

  /**
    *
    * @param from from date
    * @param to to date
    * @return the price details that are inbetween from and two dates
    */
  def between(from: util.Date, to: util.Date): String = {
    paersePriceJson(priceDao.getPriceBetweenDate(sqlDate(new DateTime(from)), sqlDate(new DateTime(to))))
  }


  /**
    *
    * @return will return past week stats 7 days ago till today
    */
  def lastWeek: String = {
    paersePriceJson(priceDao.getPriceBetweenDate(sqlDate(new DateTime().minusDays(7)), sqlDate(new DateTime())))
  }

  /**
    *
    * @return will return the past month stats till today
    */
  def lastMont: String = {
    paersePriceJson(priceDao.getPriceBetweenDate(sqlDate(new DateTime().minusMonths(1)), sqlDate(new DateTime())))
  }

  /**
    * This will refresh the Database values Prices and Currency with the new Valyes returned via the Bit-coin Web service
    * @return
    */
  def refresh(): String = {
    val body = getBody(ws.url("https://www.coinbase.com/api/v2/prices/BTC-USD/historic?period=year").get())
    val obj = parsJson(body)
    priceDao.delete
    for (price <- obj.data.prices)
      priceDao.create(price)
    currencyDao.delete
    currencyDao.create(obj.data)
    "refreshed"
  }

  private def getBody(future: Future[WSResponse]) = {
    val response = Await.result(future, Duration.Inf)
    if (response.status != 200)
      throw new Exception(response.statusText);
    response.body
  }

  private def parsJson(jsonS: String): BitCoinContainer = {
    val jsonSObj = new JsonParser().parse(jsonS).getAsJsonObject
    val obj: BitCoinContainer = gson.fromJson(jsonSObj, classOf[BitCoinContainer])
    obj
  }

}
