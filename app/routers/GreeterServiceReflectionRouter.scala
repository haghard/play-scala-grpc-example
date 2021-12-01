package routers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import example.myapp.helloworld.grpc.GreeterService
import play.grpc.internal.PlayRouter

import javax.inject.Inject
import scala.concurrent.Future

class GreeterServiceReflectionRouter @Inject()(system: ActorSystem)
  extends PlayRouter("grpc.reflection.v1alpha.ServerReflection/ServerReflectionInfo") {

  override protected val respond: HttpRequest => Future[HttpResponse] =
    akka.grpc.scaladsl.ServerReflection(List(GreeterService))(system)
}
