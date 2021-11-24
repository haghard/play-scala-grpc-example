/*
import play.api.inject.Binding
import play.api.{Configuration, Environment}

import example.myapp.helloworld.grpc._

class AkkaGrpcClientModule2 extends play.api.inject.Module {

  override def bindings(environment: Environment, configuration: Configuration): Seq[Binding[_]] = {
    //example.myapp.helloworld.grpc.GreeterServiceHandler()
        //GreeterServiceHandler.withServerReflection(new GreeterServiceImpl(system))

      //GreeterServiceHandler(this, GreeterService.name, eHandler)(system)

        /*val service: HttpRequest => Future[HttpResponse] =*/
      /*akka.grpc.scaladsl.ServiceHandler.concatOrNotFound(
        GreeterServiceHandler.partial(new GreeterServiceImpl(???)),
        akka.grpc.scaladsl.ServerReflection.partial(List(GreeterService))
      )*/
    
    Seq(bind[GreeterServiceClient].toProvider[GreeterServiceClientProvider])
  }
}
*/