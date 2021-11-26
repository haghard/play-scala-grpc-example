
package routers

import akka.actor.ActorSystem
import akka.grpc.scaladsl.GrpcExceptionHandler.defaultMapper
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import example.myapp.helloworld.grpc.{GreeterService, GreeterServiceHandler}
import play.grpc.internal.PlayRouter

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

abstract class AbstractGreeterServiceGrpcRouter(system: ActorSystem, eHandler: ActorSystem => PartialFunction[Throwable, akka.grpc.Trailers] = defaultMapper)
  extends PlayRouter(GreeterService.name)
    with GreeterService {

  protected implicit val as = system
  protected implicit val ec = system.dispatcher

  protected val terminationDeadline =
    as.settings.config.getDuration("akka.coordinated-shutdown.default-phase-timeout", java.util.concurrent.TimeUnit.SECONDS).seconds

  protected val grpcHost = system.settings.config.getString(s"""akka.grpc.client.\"${GreeterService.name}\".host""")

  protected val grpcPort = system.settings.config.getInt(s"akka.grpc.client.\"${GreeterService.name}\".port")

  override protected val respond: HttpRequest => Future[HttpResponse] =
    GreeterServiceHandler.withServerReflection(this)(system)
}