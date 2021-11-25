enablePlugins(BuildInfoPlugin)
val playGrpcV = "0.9.1"
buildInfoKeys := Seq[BuildInfoKey]("playGrpcVersion" -> playGrpcV)
buildInfoPackage := "play.scala.grpc.sample"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")

//addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "1.0.3")
//addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "1.1.1")

addSbtPlugin("com.lightbend.akka.grpc" %% "sbt-akka-grpc" % "2.1.1")
addSbtPlugin("com.lightbend.sbt" % "sbt-javaagent" % "0.1.5")

libraryDependencies += "com.lightbend.play" %% "play-grpc-generators" % playGrpcV