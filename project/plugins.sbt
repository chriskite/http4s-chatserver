addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.1")

addSbtPlugin("org.portable-scala" % "sbt-scalajs-crossproject" % "1.0.0")

addSbtPlugin("org.scala-js" % "sbt-scalajs" % "1.0.1")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.9.0")

// Creates a jar from the source code: https://github.com/sbt/sbt-native-packager
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.7.0")

// Lets us use docker compose from SBT: sbt; project appJVM; dockerComposeUp
// https://github.com/Tapad/sbt-docker-compose
addSbtPlugin("com.tapad" % "sbt-docker-compose" % "1.0.35")
