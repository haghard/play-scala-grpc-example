/*
package routers

import akka.actor.ActorSystem
import akka.grpc.scaladsl.GrpcExceptionHandler.defaultMapper
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import example.myapp.helloworld.grpc.{GreeterService, GreeterServiceHandler}
import play.grpc.internal.PlayRouter

import scala.concurrent.Future


/**
 * Abstract base class for implementing GreeterService and using as a play Router
 */
abstract class AbstractGreeterServiceRouter2(system: ActorSystem, eHandler: ActorSystem => PartialFunction[Throwable, akka.grpc.Trailers] = defaultMapper)
  extends PlayRouter(GreeterService.name)
  with GreeterService {

  implicit val sys = system

  override protected val respond: HttpRequest => Future[HttpResponse] = {
    sys.log.error("!!!!!!!!!!!!!!!!!!AbstractGreeterServiceRouter2!!!!!!!!!!!!!!!!!!!!")
    //GreeterServiceHandler(this, GreeterService.name, eHandler)(system)
    akka.grpc.scaladsl.ServiceHandler.concatOrNotFound(
      GreeterServiceHandler.partial(this),
      akka.grpc.scaladsl.ServerReflection.partial(List(GreeterService))
    )
  }
}
*/
