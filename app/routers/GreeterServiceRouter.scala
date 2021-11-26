package routers

import akka.actor.CoordinatedShutdown.Reason
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.stream.Materializer
import example.myapp.helloworld.grpc.{HelloReply, HelloRequest}

import javax.inject.Inject
import scala.concurrent.Future
import scala.util.{Failure, Success}

object GreeterServiceRouter {
  case object GrpcBoundFailure extends Reason
}

class GreeterServiceRouter @Inject()(mat: Materializer, system: ActorSystem)
  extends routers.AbstractGreeterServiceRouter(system) {
  //extends example.myapp.helloworld.grpc.AbstractGreeterServiceRouter(system) {

  system.log.info("★ ★ ★ Starting gRPC server on {}:{} ★ ★ ★", grpcHost, grpcPort)

  //TODO: Find a proper place for that
  Http()
    .newServerAt(interface = grpcHost, port = grpcPort)
    .bind(respond)
    .map(_.addToCoordinatedShutdown(terminationDeadline))
    .onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.info("★ ★ ★ Stared gRPC server bounded to {}:{} ★ ★ ★", address.getHostString, address.getPort)
      case Failure(ex) =>
        system.log.error(ex, "Failed to bind gRPC endpoint, terminating system")
        CoordinatedShutdown(as).run(GreeterServiceRouter.GrpcBoundFailure)
        //system.terminate()
    }

  override def sayHello(in: HelloRequest): Future[HelloReply] =
    Future.successful(HelloReply(s"Hello, ${in.name}!"))
}
