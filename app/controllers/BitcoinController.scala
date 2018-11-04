package controllers

import java.util.Date
import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.Forms.{mapping, number}
import play.api.data.validation.Constraints.{max, min}
import play.api.mvc._
import services.BitcoinService

import scala.concurrent.{ExecutionContext, Future}

/**
  */
@Singleton
class BitcoinController @Inject()(service: BitcoinService,
                                  cc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val dateInputForm : Form[DateInputForm] = Form{
    mapping(
      "from"-> date,
      "to"-> date
    )(DateInputForm.apply)(DateInputForm.unapply)
  }

  /**
    * The index action.
    */
  def index = Action { implicit request =>
    service.refresh
    Ok(views.html.price_index(dateInputForm))
  }

  /**
    *
    * @return
    */
  def searchPrice = Action.async { implicit request =>
    dateInputForm.bindFromRequest.fold(
      errorForm => {
        Future.successful(Ok(views.html.price_index(errorForm)))
      },date => {
        val from = date.from
        val to  = date.to
        Future.successful(Ok(service.between(from,to)))
      }
    )
  }

  /**
    */
  def refresh = Action {
    Ok(service.refresh)
  }

  def lastMonth = Action {
    Ok(service.lastMont)
  }

  def lastWeek = Action {
    Ok(service.lastWeek)
  }

  def todaysPrice = Action {
    Ok(service.todaysPrice)
  }

}

case class DateInputForm(from: Date, to: Date)


