import play.core.PlayVersion.akkaVersion
import play.core.PlayVersion.akkaHttpVersion
import play.grpc.gen.scaladsl.{PlayScalaClientCodeGenerator, PlayScalaServerCodeGenerator}
import com.typesafe.sbt.packager.docker.{Cmd, CmdLike, DockerAlias, ExecCmd}
import play.core.PlayVersion
import play.scala.grpc.sample.BuildInfo

name := "play-scala-grpc-example"
version := "1.0-SNAPSHOT"


lazy val `play-scala-grpc-example` = (project in file("."))
  .enablePlugins(PlayScala)
  .enablePlugins(AkkaGrpcPlugin) // enables source generation for gRPC
  //.enablePlugins(PlayAkkaHttp2Support) // enables serving HTTP/2 and gRPC
  // #grpc_play_plugins
  .settings(
    akkaGrpcGeneratedLanguages := Seq(AkkaGrpc.Scala),
    // #grpc_client_generators
    // build.sbt
    akkaGrpcExtraGenerators += PlayScalaClientCodeGenerator,
    // #grpc_client_generators
    // #grpc_server_generators
    // build.sbt
    akkaGrpcExtraGenerators += PlayScalaServerCodeGenerator,
    // #grpc_server_generators
    PlayKeys.devSettings ++= Seq(
      "play.server.http.port" -> "8080"
    )
  )
  .settings(
    // workaround to https://github.com/akka/akka-grpc/pull/470#issuecomment-442133680
    dockerBaseImage := "openjdk:8-alpine",
    dockerCommands :=
      Seq.empty[CmdLike] ++
        Seq(
          Cmd("FROM", "openjdk:8-alpine"),
          ExecCmd("RUN", "apk", "add", "--no-cache", "bash")
        ) ++
        dockerCommands.value.tail,
    Docker / dockerAliases += DockerAlias(None, None, "play-scala-grpc-example", None),
    Docker / packageName := "play-scala-grpc-example",
  )
  .settings(
    libraryDependencies ++= CompileDeps ++ TestDeps
  )

val akkaHttpVer = "10.2.7"
val CompileDeps = Seq(
  guice,
  "com.lightbend.play" %% "play-grpc-runtime" % BuildInfo.playGrpcVersion, //0.9.1
  "com.typesafe.akka" %% "akka-discovery" % PlayVersion.akkaVersion,
  "com.typesafe.akka" %% "akka-http" % PlayVersion.akkaHttpVersion,


  //You can solve this by adding an explicit dependency on version 10.2.7 of the [akka-http-spray-json, akka-http2-support] artifacts to your project.
  "com.typesafe.akka" %% "akka-http2-support"   %  akkaHttpVer, //PlayVersion.akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" %  akkaHttpVer, //PlayVersion.akkaHttpVersion,

  "com.thesamet.scalapb" %% "scalapb-json4s" % "0.11.0",
  
  //current: 3.11.4
  //"com.google.protobuf" % "protobuf-java-util" % "3.19.1",
  //"com.google.protobuf" % "protobuf-java"      % "3.19.1",
  //"com.lihaoyi" % "ammonite" % "2.4.1" % "test" cross CrossVersion.full,

  // Test Database
  "com.h2database" % "h2" % "1.4.199"
)

val playVersion = play.core.PlayVersion.current
val TestDeps = Seq(
  "com.lightbend.play" %% "play-grpc-scalatest" % BuildInfo.playGrpcVersion % Test,
  "com.lightbend.play" %% "play-grpc-specs2" % BuildInfo.playGrpcVersion % Test,
  "com.typesafe.play" %% "play-test" % playVersion % Test,
  "com.typesafe.play" %% "play-specs2" % playVersion % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
)

val genPlayArtifacts = taskKey[Unit]("Generate Play artifacts (routes and controllers) using gRPC definition")

genPlayArtifacts := Def.taskDyn {

  // standart Play project paths
  val defaultAppFolder = "./app"
  val defaultConfFolder = "./conf"
  val defaultProtobufFolder = "./app/protobuf"

  val Sep = "#"
  val targetControllersPackName = "controllers"
  val v = scalaVersion.value
  val majorMinorVer    = v.substring(0, v.lastIndexOf("."))
  val protobufDir      = file(defaultProtobufFolder)
  val confFolder       = file(defaultConfFolder)
  val appDir           = file(defaultAppFolder)
  val classesTargetDir = (Compile / target).value / s"scala-$majorMinorVer" / "akka-grpc" / "main"
  //(runMain in Compile).toTask(s" gateway.Main ${targetDir.absolutePath}").value
  
  val input = s"${classesTargetDir.absolutePath}${Sep}${protobufDir.absolutePath}${Sep}${appDir.getAbsolutePath}${Sep}${confFolder.getAbsolutePath}${Sep}${targetControllersPackName}"
  if (classesTargetDir.exists() && protobufDir.exists()) {
    Def.task { (Compile / runMain).toTask(s" gateway.PlayArtifactsGenerator $input").value }
  } else Def.task(println(s"Some paths are missing. Maybe try to compile first $input"))

  /*
  val javaHome = sys.env("JAVA_HOME")
  val input = ${classesTargetDir.absolutePath}#${protobufDir.absolutePath}#${appfDir.getAbsolutePath}
  val code = scala.sys.process.Process(Seq(s"$javaHome/bin/java", "-jar", "gateway.jar", input)).!
  println(s"keytool return code: $code")
  if (code == 0) println(s"XXX has been generated") else println("Oooops something went wrong!")
  */

}.value

addCommandAlias("c", "compile")

//test:run
Test / sourceGenerators += Def.task {
  val file = (Test / sourceManaged).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main().run() }""")
  Seq(file)
}.taskValue


scalaVersion := "2.13.7"
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")

// Make verbose tests
Test / testOptions := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))