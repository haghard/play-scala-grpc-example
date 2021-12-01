package controllers

import com.typesafe.config.Config

import javax.inject.Inject
import play.api.mvc._

import example.myapp.helloworld.grpc._

import scala.util.control.NonFatal

import scala.concurrent.{ExecutionContext, Future}

class GreeterServiceController @Inject()(config: Config)(implicit ec: ExecutionContext)
  extends InjectedController {
  /*
  import play.api.data.Forms._
   import play.api.data.Form
  private def form: Form[HelloRequest] = ???
   Form(mapping("name" -> nonEmptyText)(HelloRequest(_))(r => Some(r.name)))
  */

 private def parse(req: Request[AnyContent]): Either[String, HelloRequest] =
   req.body.asJson match {
     case Some(json) =>
       val req: Either[String, HelloRequest] =
         try Right(scalapb.json4s.JsonFormat.fromJsonString[HelloRequest](json.toString()))
         catch {
           case NonFatal(ex) => Left(ex.getMessage)
         }
       req
     case None => Left("Empty body")
   }

  def sayHello() = Action.async { implicit req =>
    val f: Future[Result] = Future {
      //val parseResult = form.bindFromRequest()
      //val reply: HelloReply = parseResult.fold(???, ???)

      val reply: HelloReply = parse(req) match {
        case Right(pb) => HelloReply("Hello " + pb.name + ":" + pb.age)
        case Left(error) => HelloReply(error)
      }

      Ok(reply.message)
    }

    f
  }
}

