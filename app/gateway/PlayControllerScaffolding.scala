package gateway

trait PlayControllerScaffolding {

  def controllerHeader(cntPkgName: String, packageName: String, controllerName: String): String =
    s"""package $cntPkgName
       |
       |import com.typesafe.config.Config
       |
       |import javax.inject.Inject
       |import play.api.mvc._
       |
       |import $packageName._
       |
       |import scala.util.control.NonFatal
       |
       |import scala.concurrent.{ExecutionContext, Future}
       |
       |import com.google.api.HttpBody
       |
       |class $controllerName @Inject()(config: Config)(implicit ec: ExecutionContext)
       |  extends InjectedController {
       |
       |  private def jsonBodyToProto[T <: scalapb.GeneratedMessage: scalapb.GeneratedMessageCompanion](
       |    req: Request[AnyContent]
       |  ): Either[String, T] =
       |    req.body.asJson match {
       |      case Some(json) =>
       |        val req: Either[String, T] =
       |          try Right(scalapb.json4s.JsonFormat.fromJsonString[T](json.toString()))
       |          catch {
       |            case NonFatal(ex) => Left(ex.getMessage)
       |          }
       |        req
       |      case None => Left("Empty body")
       |    }
       |
      """.stripMargin

  def controllerPostMethod(methodName: String, request: String, response: String): String =
    s"""
       | def $methodName () = Action.async { implicit req =>
       |   val f: Future[Result] = Future {
       |     val reply: $response = jsonBodyToProto[$request](req) match {
       |       case Right(pb) => $response(pb.toProtoString)
       |       case Left(error) => $response(error)
       |     }
       |     Ok(reply.toProtoString)
       |   }
       |   f
       | }
       |""".stripMargin

  def controllerGetMethod(
    methodName: String, response: String,
    pathParams: Map[String, String] = Map.empty,
    queryParams: Map[String, String] = Map.empty
  ): String =
    if (pathParams.isEmpty && queryParams.isEmpty)
      s"""
         | def $methodName () = Action { implicit req =>
         |   val r: $response = ???
         |
         |   Ok(r.toProtoString)
         | }
         |""".stripMargin
    else
      s"""
         | def $methodName (${pathParams.map { case (m, t) => s"$m: $t" }.mkString(", ")}, ${queryParams.map { case (m, t) => s"$m: $t" }.mkString(", ")}) = Action { implicit req =>
         |   val r: $response = ???
         |
         |   Ok(r.toProtoString)
         | }
         |""".stripMargin

  def controllerFooter(): String = "\n}"

}
