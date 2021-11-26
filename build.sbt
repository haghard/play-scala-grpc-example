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
        "play.server.http.port" -> "8080",
      )
    )
    .settings(
      // workaround to https://github.com/akka/akka-grpc/pull/470#issuecomment-442133680
      dockerBaseImage := "openjdk:8-alpine",
      dockerCommands  := 
        Seq.empty[CmdLike] ++
        Seq(
          Cmd("FROM", "openjdk:8-alpine"), 
          ExecCmd("RUN", "apk", "add", "--no-cache", "bash")
        ) ++
        dockerCommands.value.tail ,
      Docker / dockerAliases   += DockerAlias(None, None, "play-scala-grpc-example", None),
      Docker / packageName  := "play-scala-grpc-example",
    )
    .settings(libraryDependencies ++= CompileDeps ++ TestDeps)

val akkaHttpVersion = "10.2.7"
val CompileDeps = Seq(
  guice,
  "com.lightbend.play"      %% "play-grpc-runtime"    % BuildInfo.playGrpcVersion,
  "com.typesafe.akka"       %% "akka-discovery"       % PlayVersion.akkaVersion,
  "com.typesafe.akka"       %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka"       %% "akka-http2-support"   % akkaHttpVersion,
  // Test Database
  "com.h2database" % "h2" % "1.4.200"
)

val playVersion = play.core.PlayVersion.current

val TestDeps = Seq(
  "com.lightbend.play"      %% "play-grpc-scalatest" % BuildInfo.playGrpcVersion % Test, 
  "com.lightbend.play"      %% "play-grpc-specs2"    % BuildInfo.playGrpcVersion % Test, 
  "com.typesafe.play"       %% "play-test"           % playVersion     % Test, 
  "com.typesafe.play"       %% "play-specs2"         % playVersion     % Test, 
  "org.scalatestplus.play"  %% "scalatestplus-play"  % "5.1.0" % Test,
)

scalaVersion := "2.13.7"
scalacOptions ++= List("-encoding", "utf8", "-deprecation", "-feature", "-unchecked")

// Make verbose tests
Test / testOptions := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))