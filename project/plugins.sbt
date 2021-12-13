enablePlugins(BuildInfoPlugin)

val playGrpcV = "0.9.1"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.scala.grpc.sample"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.11")

addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "2.1.1")

libraryDependencies ++= Seq(
  "com.lightbend.play" %% "play-grpc-generators" % playGrpcV,

  //local build with https://github.com/akka/akka-grpc/pull/1511
  "com.ncl" % "play-grpc-gateway" % "0.0.1"
    from "file:///Volumes/dev/projects/yoppworks/NCL/play-grpc-gateway/target/scala-2.12/play-grpc-gateway_2.12-0.0.1.jar",

  //local build with https://github.com/akka/akka-grpc/pull/1511
  "com.lightbend.akka.grpc" %% "akka-grpc-codegen" % "2.1.2"
    from """file:///Users/haghard/.ivy2/local/com.lightbend.akka.grpc/akka-grpc-codegen_2.12/2.1.2/jars/akka-grpc-codegen_2.12.jar""",
)
