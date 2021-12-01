/*

package controllers

import akka.actor.ActorSystem
import com.typesafe.config.Config
import example.myapp.helloworld.grpc.{HelloReply, HelloRequest}

import javax.inject.Inject
import play.api.mvc._

import play.api.data.Forms._
import play.api.data.Form


//import scalapb.json4s.JsonFormat

import scala.concurrent.{ExecutionContext, Future}

final class GreeterServiceController2 @Inject()(config: Config, sys: ActorSystem)(implicit ec: ExecutionContext) extends InjectedController {
  val logger = sys.log


  private def form: Form[HelloRequest] = Form(
    mapping("name" -> nonEmptyText /*, "price" -> number(min = 0)*/)(HelloRequest(_))(r => Some(r.name))
  )

  def sayHello() = Action.async { implicit req /*: MessagesRequest[AnyContent]*/ =>

    val f: Future[Result] = Future {
      val parseResult = form.bindFromRequest()
      val reply: HelloReply = parseResult.fold({ err => HelloReply("Hello none") }, { suc => HelloReply(s"Hello ${suc.name}") })
      /*val reply =
        req.body.asJson match {
          case Some(json) =>
            logger.warning("{}: json {}", parseResult, json.toString())
          case None =>
            HelloReply("Hello ")
        }*/
      Ok(reply.message)
    }

    f
  }
}

*/
