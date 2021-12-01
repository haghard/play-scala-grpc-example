package binders

import play.api.mvc.PathBindable
//import models.Article
import example.myapp.helloworld.grpc.HelloRequest


object Binders {

  implicit def a(implicit strBinder: PathBindable[String]) = new PathBindable[HelloRequest] {
    override def bind(key: String, value: String): Either[String, HelloRequest] = {
      strBinder.bind(key, value)
      val r: Either[String, HelloRequest] =  Right(HelloRequest(value))
      r
    }
    override def unbind(key: String, value: HelloRequest): String =
      strBinder.unbind(key, value.name)
  }

  //https://www.playframework.com/documentation/2.8.x/api/scala/play/api/mvc/PathBindable.html
  
  //http://julien.richard-foy.fr/blog/2012/04/09/how-to-implement-a-custom-pathbindable-with-play-2/
  
  //case class Article(id: Long, name: String, price: Double)
  /*implicit def articlePathBindable(implicit longBinder: PathBindable[Long]) = new PathBindable[HelloRequest] {

    def bind(key: String, value: String): Either[String, HelloRequest] =
      for {
        id <- longBinder.bind(key, value).right
        article <- HelloRequest.findById(id).toRight("Article not found").right
      } yield article

    def unbind(key: String, article: HelloRequest): String =
      longBinder.unbind(key, article.id)
  }*/

}
