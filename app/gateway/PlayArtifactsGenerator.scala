package gateway

import java.io.{File, FileOutputStream}
import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Using
import scala.util.control.NonFatal
import com.google.common.reflect.ClassPath

/**
 * Take a look https://github.com/OpenHFT/Java-Runtime-Compiler
 *
 */
object PlayArtifactsGenerator extends App {
  val SEP = "#"
  val cntPkgName = "controllers"
  val routesFileName = "routes"
  val Pref = "Service"
  val JavaPackageTag = "java_package"

  //option java_package = "example.myapp.helloworld.grpc";
  val JavaPackageExp = s"""option(.*)${JavaPackageTag}(.*)=(.*)""".r

  //println(s"""★ ★ ★ Main: ${args.mkString(",")} ★ ★ ★""")

  try {
    val paths = args(0).split(SEP)
    //val classesTargetDir = Paths.get(paths(0)).toFile.exists()
    val protobufDir = Paths.get(paths(1)).toFile
    val appDir = Paths.get(paths(2))
    val confDir = Paths.get(paths(3))
    val protoFiles = protobufDir.listFiles((_: File, name: String) => name.endsWith(".proto"))

    val maybePackageName: Option[String] =
      if (protoFiles.size == 1) {
        val schemaFile = protoFiles(0)
        println(s"★ ★ ★ Protobuf schema file: ${schemaFile.getAbsolutePath} ★ ★ ★")
        val javaPackages =
          Using.resource(java.nio.file.Files.newBufferedReader(schemaFile.toPath)) { in =>
            in.lines().filter(_.contains(JavaPackageTag)).collect(Collectors.toList[String])
          }.asScala.toSeq
        javaPackages.headOption
      } else None

    val packageLine = maybePackageName.getOrElse(throw new Exception("Failed to load package name."))
    val packageName = packageLine match {
      case JavaPackageExp(_, _, name) => name.replace("\"", "").replace(";", "").trim
      case _ => throw new Exception("Failed to extract package name.")
    }

    println(s"★ ★ ★ PackageName: $packageName ★ ★ ★")

    val cl = this.getClass.getClassLoader
    val services = ClassPath.from(cl).getTopLevelClasses(packageName)
      .stream().filter(_.getName.endsWith(Pref))
      .collect(Collectors.toList[ClassPath.ClassInfo]).asScala.toSeq

    val serviceInfo = services.headOption.getOrElse(throw new Exception("Failed to load service"))
    val serviceName = serviceInfo.getSimpleName
    val controllerName = s"${serviceName}Controller"
    val newControllerFile = s"${appDir.toFile.getAbsolutePath}/$cntPkgName/$controllerName.scala"
    val routesFile = s"${confDir.toFile.getAbsolutePath}/$routesFileName"

    Files.deleteIfExists(Paths.get(newControllerFile))
    Files.deleteIfExists(Paths.get(routesFile))

    val serviceClass = Class.forName(serviceInfo.getName)
    val methods = serviceClass.getMethods
    //example.myapp.helloworld.grpc.GreeterService.descriptor
    val descriptorMethod = methods.find(_.getName.contains("descriptor")).getOrElse(throw new Exception(s"Couldn't find descriptor on ${serviceInfo.getName}"))
    val fileDescriptor = descriptorMethod.invoke(serviceClass).asInstanceOf[com.google.protobuf.Descriptors.FileDescriptor]

    val servicesIt = fileDescriptor.getServices.iterator()
    //we support just one service for now
    if(servicesIt.hasNext) {
      val sd = servicesIt.next()

      sd.getMethods.forEach { serviceMethod =>

        println(s"★ ★ ★ Generating play controller $packageName.$controllerName ★ ★ ★")
        Using.resource(new FileOutputStream(newControllerFile))(_.write(
            genPlayController(
              packageName,
              controllerName,
              serviceMethod.getName,
              serviceMethod.getInputType.getName,
              serviceMethod.getOutputType.getName
            )
          ))

        println(s"★ ★ ★ Generating $routesFile ★ ★ ★")
        Using.resource(new FileOutputStream(routesFile))(_.write(genPlayRoutesFile(controllerName, serviceMethod.getName)))
      }
    }

  } catch {
    case NonFatal(ex) =>
      println(s" ${ex.getMessage}")
      System.exit(-1)
  }

  private def genPlayRoutesFile(controllerName: String, method: String): Array[Byte] =
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

  private def genPlayController(
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
       |  private def parse[T <: scalapb.GeneratedMessage with scalapb.Message[T] : scalapb.GeneratedMessageCompanion](req: Request[AnyContent]): Either[String, T] =
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
       |  def ${methodName}() = Action.async { implicit req =>
       |    val f: Future[Result] = Future {
       |      //val parseResult = form.bindFromRequest()
       |      //val reply: ${response} = parseResult.fold(???, ???)
       |
       |      val reply: ${response} = parse[${request}](req) match {
       |        case Right(pb) => ${response}("Hello " + pb.name + ":" + pb.age)
       |        case Left(error) => ${response}(error)
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