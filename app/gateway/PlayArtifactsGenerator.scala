package gateway

import com.google.common.reflect.ClassPath

import java.io.{File, FileOutputStream}
import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Paths}
import java.util.stream.Collectors
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.util.Using
import scala.util.control.NonFatal

/**
 * 
 *
 */
object PlayArtifactsGenerator extends App with PlayControllerScaffolding with PlayRoutesScaffolding {
  val SEP = "#"
  val routesFileName = "routes"
  val Pref = "Service"
  val JavaPackageTag = "java_package"
  val PackageTag = "package"

  val PackageExp = """package(.*)""".r
  val JavaPackageExp = s"""option(.*)${JavaPackageTag}(.*)=(.*)""".r

  //println(s"""★ ★ ★ Main: ${args.mkString(",")} ★ ★ ★""")

  try {
    val paths = args(0).split(SEP)
    //val classesTargetDir = Paths.get(paths(0)).toFile.exists()
    val protobufDir = Paths.get(paths(1)).toFile
    val appDir = Paths.get(paths(2))
    val confDir = Paths.get(paths(3))
    val protoFiles = protobufDir.listFiles((_: File, name: String) => name.endsWith(".proto"))
    val targetControllersPackName = paths(4)

    val maybePackageName: Option[String] =
      if (protoFiles.size == 1) {
        val schemaFile = protoFiles(0)
        println(s"★ ★ ★ Protobuf schema file: ${schemaFile.getAbsolutePath} ★ ★ ★")
        val javaPackages =
          Using.resource(java.nio.file.Files.newBufferedReader(schemaFile.toPath)) { in =>
            in.lines().filter { line => !line.contains("""//""") && (line.contains(JavaPackageTag) || line.startsWith(PackageTag)) }
              .collect(Collectors.toList[String])
          }.asScala.toSeq

        val javaPackagesLine = javaPackages.size match {
          case 1 =>
            javaPackages.head
          case 2 =>
            val a = javaPackages.head
            val b = javaPackages.tail.head
            println(""" "option" java_package takes precedence over "package" """)
            if (a.contains(JavaPackageTag)) a else b
          case _ =>
            throw new Exception("Smth's wrong with protobuf package definition")
        }
        val packageName = javaPackagesLine match {
          case JavaPackageExp(_, _, name) => name.replace("\"", "").replace(";", "").trim
          case PackageExp(name) => name.replace("\"", "").replace(";", "").trim
          case _ => throw new Exception("Failed to extract package name.")
        }
        Some(packageName)

      } else None

    val packageName = maybePackageName.getOrElse(throw new Exception("Failed to load package name."))
    println(s"★ ★ ★ PackageName: $packageName ★ ★ ★")

    val cl = this.getClass.getClassLoader
    val services = ClassPath.from(cl).getTopLevelClasses(packageName)
      .stream().filter(_.getName.endsWith(Pref))
      .collect(Collectors.toList[ClassPath.ClassInfo]).asScala.toSeq

    val serviceInfo = services.headOption.getOrElse(throw new Exception("Failed to load service"))
    val serviceName = serviceInfo.getSimpleName
    val controllerName = s"${serviceName}Controller"
    val controllerFile = s"${appDir.toFile.getAbsolutePath}/$targetControllersPackName/$controllerName.scala"
    val routesFile = s"${confDir.toFile.getAbsolutePath}/$routesFileName"

    Files.deleteIfExists(Paths.get(controllerFile))
    Files.deleteIfExists(Paths.get(routesFile))

    //
    val serviceClass = Class.forName(serviceInfo.getName)
    val methods = serviceClass.getMethods

    //GreeterService.descriptor
    val descriptorMethod = methods.find(_.getName.contains("descriptor")).getOrElse(throw new Exception(s"Couldn't find descriptor on ${serviceInfo.getName}"))
    val fileDescriptor = descriptorMethod.invoke(serviceClass).asInstanceOf[com.google.protobuf.Descriptors.FileDescriptor]


    val cBuffer = new StringBuilder()
    cBuffer.append(cntrHeader(targetControllersPackName, packageName, controllerName))

    val rBuffer = new StringBuilder()
    rBuffer.append(routesHeader())

    val servicesIt = fileDescriptor.getServices.iterator()

    //we support just one service for the time being
    if (servicesIt.hasNext) {
      val sd = servicesIt.next()
      sd.getMethods.forEach { serviceMethod =>
        cBuffer.append(cntrlMethod(serviceMethod.getName, serviceMethod.getInputType.getName, serviceMethod.getOutputType.getName))
        rBuffer.append(routesRoute(targetControllersPackName, controllerName, serviceMethod.getName))
        println(s"★ ★ ★ Generating a Play controller method $packageName.$controllerName ${serviceMethod.getName} ★ ★ ★")
      }
    }

    cBuffer.append(cntrFooter())
    rBuffer.append(routesFooter(targetControllersPackName))

    //TODO: consider writing in chunks
    Using.resource(new FileOutputStream(controllerFile))(_.write(cBuffer.toString().getBytes(StandardCharsets.UTF_8)))
    Using.resource(new FileOutputStream(routesFile))(_.write(rBuffer.toString().getBytes(StandardCharsets.UTF_8)))

  } catch {
    case NonFatal(ex) =>
      println(s" ${ex.getMessage}")
      System.exit(-1)
  }
}