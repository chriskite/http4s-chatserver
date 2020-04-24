import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

/**
 * Lists the dependencies for this project.
 */
object Dependencies {

  private val http4sVersion     = "0.21.0"
  private val logbackVersion    = "1.2.3"
  private val uPickleVersion    = "0.9.9"
  private val scalaJsDomVersion = "1.0.0"
  private val scalaJsLoggingVersion = "1.0.1"

  object client {
    // The triple % gets the library in two versions: One for running on the JVM and one for running on a JavaScript engine like V8
    val scalaJsDependencies = Def.setting(
      Seq(
        // Serializes data between client and server: https://github.com/lihaoyi/upickle-pprint
        "com.lihaoyi" %%% "upickle" % uPickleVersion,
        "org.scala-js" %%% "scalajs-dom" % scalaJsDomVersion,
        "org.scala-js" %%% "scalajs-logging" % scalaJsLoggingVersion
      )
    )
  }

  val server = {
    Def.setting(
      Seq(
        // Serializes data between client and server: https://github.com/lihaoyi/upickle-pprint
        "com.lihaoyi" %% "upickle" % uPickleVersion,
        "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
        "org.http4s"     %% "http4s-dsl"          % http4sVersion,
        "ch.qos.logback" % "logback-classic"      % logbackVersion
      )
    )
  }
}
