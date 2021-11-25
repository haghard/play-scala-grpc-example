package routers

import akka.actor.CoordinatedShutdown.Reason
import akka.actor.{ActorSystem, CoordinatedShutdown}
import akka.http.scaladsl.Http
import akka.stream.Materializer
import example.myapp.helloworld.grpc.{AbstractGreeterServiceRouter, HelloReply, HelloRequest}

import javax.inject.Inject
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object HelloWorldRouter {
  case object GrpcBoundFailure extends Reason
}

class HelloWorldRouter @Inject()(mat: Materializer, system: ActorSystem)
  extends AbstractGreeterServiceGrpcRouter(system) {
  //extends AbstractGreeterServiceRouter(system) {

  implicit val as = system
  implicit val ec = system.dispatcher
  system.log.warning("★ ★ ★ Starting gRPC server on {}:{} ★ ★ ★", grpcHost, grpcPort)

  //TODO: Find a proper place to start this
  Http(system)
    .newServerAt(interface = grpcHost, port = grpcPort)
    .bind(respond)
    .map(_.addToCoordinatedShutdown(hardTerminationDeadline = 5.seconds))
    .onComplete {
      case Success(binding) =>
        val address = binding.localAddress
        system.log.warning("★ ★ ★ Stared gRPC server bounded to {}:{} ★ ★ ★", address.getHostString, address.getPort)
      //println(s"gRPC server bound to ${address.getHostString}:${address.getPort}")
      case Failure(ex) =>
        system.log.error(ex, "Failed to bind gRPC endpoint, terminating system")
        CoordinatedShutdown(as).run(HelloWorldRouter.GrpcBoundFailure)
        //system.terminate()
    }

  override def sayHello(in: HelloRequest): Future[HelloReply] =
    Future.successful(HelloReply(s"Hello, ${in.name}!"))
}
