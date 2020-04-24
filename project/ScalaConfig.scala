import sbt.Def

/**
 * Configures Scala for this project.
 */
object ScalaConfig {

  def compilerOptions(inDevMode: Boolean) = Def.setting(commonCompilerOptions)

  /** Scala compiler options used for both development and production mode */
  private val commonCompilerOptions =
    Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      // Warn when we use advanced language features
      "-feature",
      // Give more information on type erasure warning
      "-unchecked",
      // Don't warn when we use these language features
      "-language:postfixOps",
      "-language:implicitConversions",
      "-Ywarn-dead-code",
      "-Ywarn-unused",
      "-Xlint"
    )

  val version = "2.13.2"
}
