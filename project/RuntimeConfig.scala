/**
 * Configures the Java runtime for this project.
 *
 * SBT Native Packager lets us configure the JVM using these flags: http://www.scala-sbt.org/sbt-native-packager/archetypes/java_app/index.html#start-script-options
 */
object RuntimeConfig {

  def debugOptions(inDevMode: Boolean) =
    if (inDevMode)
      Seq(
        "-jvm-debug 5005"
      )
    else Seq()

  def javaRuntimeOptions = Seq(
  )
}
