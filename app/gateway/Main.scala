package gateway

import java.io.{File, FileOutputStream, FilenameFilter, IOException}
import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Using
import scala.util.control.NonFatal
import com.google.common.reflect.ClassPath

object Main extends App {
  val SEP = "#"
  val cntPkgName = "controllers"
  val routesFileName = "routes"
  val Pref = "Service"
  println(s"""★ ★ ★ Main: ${args.mkString(",")} ★ ★ ★""")
  try {

    val paths = args(0).split(SEP)

    val classesTargetDir = Paths.get(paths(0)).toFile.exists()
    val protobufDir = Paths.get(paths(1)).toFile
    val appDir = Paths.get(paths(2))
    val confDir = Paths.get(paths(3))

    val protoFiles = protobufDir.listFiles((_: File, name: String) => name.endsWith(".proto"))

    val packageName0: Option[String] =
      if (protobufDir.isDirectory && protoFiles.size == 1) {
        val schemaFile = protoFiles(0)
        println("SchemaFile: " + schemaFile.getAbsolutePath)
        //TODo extract option java_package
        Some("example.myapp.helloworld.grpc")
      } else {
        None
      }

    //
    val packageName = packageName0.getOrElse[String](throw new Exception("!!!"))

    val services = ClassPath.from(this.getClass.getClassLoader).getTopLevelClasses(packageName)
      .stream().filter(_.getName.endsWith(Pref))
      .collect(Collectors.toList[ClassPath.ClassInfo]).asScala.toSeq

    val info = services.headOption.getOrElse(throw new Exception("!!!"))

    val serviceName = info.getSimpleName
    val controllerName = serviceName + "Controller"
    val newControllerFile = appDir.toFile.getAbsolutePath + "/" + cntPkgName + "/" + s"$controllerName.scala"
    val routesFile = confDir.toFile.getAbsolutePath + "/" + routesFileName

    Files.deleteIfExists(Paths.get(newControllerFile))
    Files.deleteIfExists(Paths.get(routesFile))


    /*println(s"★ ★ ★ Serice ★ ★ ★")
    val it = GreeterService.descriptor.getServices.iterator()
    while (it.hasNext) {
      val sd = it.next()
      sd.getMethods.forEach { m =>
        println(s"""service ${sd.getName} { rpc ${m.getName} (${m.getInputType.getName}) returns (${m.getOutputType.getName}) }""")
      }
    }
    println(s"★ ★ ★  ★ ★ ★")
    */

    println(s"★ ★ ★ Generating play controller $packageName.$controllerName ★ ★ ★")

    Using.resource(new FileOutputStream(newControllerFile))(_.write(
      genController(
        packageName,
        controllerName, "sayHello",
        "HelloRequest",
        "HelloReply"
      )
    ))

    println(s"★ ★ ★ Generating $routesFile ★ ★ ★")
    Using.resource(new FileOutputStream(routesFile))(_.write(genRoutes(controllerName, "sayHello")))

  } catch {
    case NonFatal(ex) =>
      ex.printStackTrace()
      System.exit(-1)
  }


  //$packageName.HelloRequest
  private def genRoutes(controllerName: String, method: String): Array[Byte] =
    s"""
       |# Routes
       |# This file defines all application routes (Higher priority routes first)
       |# ~~~~
       |
       |# An example controller showing a sample home page
       |POST     /$method    $cntPkgName.$controllerName.$method()
       |
       |# Map static resources from the /public folder to the /assets URL path
       |GET     /assets/*file        $cntPkgName.Assets.versioned(path="/public", file: Asset)
       |
       |""".stripMargin.getBytes

  private def genController(
    packageName: String, controllerName: String, methodName: String,
    request: String, response: String
  ): Array[Byte] =
    s"""package $cntPkgName
       |
       |import com.typesafe.config.Config
       |
       |import javax.inject.Inject
       |import play.api.mvc._
       |
       |import ${packageName}._
       |
       |import scala.util.control.NonFatal
       |
       |import scala.concurrent.{ExecutionContext, Future}
       |
       |class ${controllerName} @Inject()(config: Config)(implicit ec: ExecutionContext)
       |  extends InjectedController {
       |  /*
       |  import play.api.data.Forms._
       |   import play.api.data.Form
       |  private def form: Form[${request}] = ???
       |   Form(mapping("name" -> nonEmptyText)(HelloRequest(_))(r => Some(r.name)))
       |  */
       |
       | private def parse(req: Request[AnyContent]): Either[String, HelloRequest] =
       |   req.body.asJson match {
       |     case Some(json) =>
       |       val req: Either[String, HelloRequest] =
       |         try Right(scalapb.json4s.JsonFormat.fromJsonString[${request}](json.toString()))
       |         catch {
       |           case NonFatal(ex) => Left(ex.getMessage)
       |         }
       |       req
       |     case None => Left("Empty body")
       |   }
       |
       |  def ${methodName}() = Action.async { implicit req =>
       |    val f: Future[Result] = Future {
       |      //val parseResult = form.bindFromRequest()
       |      //val reply: ${response} = parseResult.fold(???, ???)
       |
       |      val reply: HelloReply = parse(req) match {
       |        case Right(pb) => HelloReply("Hello " + pb.name + ":" + pb.age)
       |        case Left(error) => HelloReply(error)
       |      }
       |
       |      Ok(reply.message)
       |    }
       |
       |    f
       |  }
       |}
       |
       |""".stripMargin.getBytes

}


/*def sandboxedClassLoader(files: Seq[File]): URLClassLoader = {
  val cloader = new URLClassLoader(
    files.map(_.toURI().toURL()).toArray,
    new FilteringClassLoader(getClass().getClassLoader())
  )
  cloader
}*/