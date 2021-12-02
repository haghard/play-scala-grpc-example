enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.9.1"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.scala.grpc.sample"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.11")

addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "2.1.1")

libraryDependencies += "com.lightbend.play" %% "play-grpc-generators" % playGrpcV
