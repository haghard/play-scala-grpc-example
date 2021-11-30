
package routers

import akka.actor.ActorSystem
import akka.grpc.scaladsl.GrpcExceptionHandler.defaultMapper
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import example.myapp.helloworld.grpc.{GreeterService, GreeterServiceHandler}
import play.grpc.internal.PlayRouter

import scala.concurrent.Future


abstract class AbstractGreeterServiceRouter2(system: ActorSystem, eHandler: ActorSystem => PartialFunction[Throwable, akka.grpc.Trailers] = defaultMapper)
  extends PlayRouter(GreeterService.name)
  with GreeterService {

  override protected val respond: HttpRequest => Future[HttpResponse] = {
    system.log.error("*********AbstractGreeterServiceRouter2*******")
    //was GreeterServiceHandler(this, GreeterService.name, eHandler)(system)
    
    /**
     * https://doc.akka.io/docs/akka-grpc/current/server/reflection.html
     *
     * This is how it's done in GreeterServiceHandler.withServerReflection
     * Assumption: This doesn't work because GRPC routing happens on the Http2ServerDemux level.
     */
    akka.grpc.scaladsl.ServiceHandler.concatOrNotFound(
      GreeterServiceHandler.partial(this, GreeterService.name, eHandler)(system),
      akka.grpc.scaladsl.ServerReflection.partial(List(GreeterService))(system)
    )
  }
}

