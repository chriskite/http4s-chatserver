/*
val scalaV = "2.13.2"

// Scala-Js frontend
lazy val frontend =
  (project in file("frontend"))
    .enablePlugins(ScalaJSPlugin)
    .settings(commonSettings: _*)
    .settings(
      scalaJSUseMainModuleInitializer := true,
      testFrameworks += new TestFramework("utest.runner.Framework"),
      libraryDependencies ++= Seq(
        "com.lihaoyi"  %%% "utest"       % utestV % Test
      )
    )
  .dependsOn(sharedJS)

lazy val backend = project
  .in(file("backend"))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
    ),
    resourceGenerators in Compile += Def.task {
      Seq((fastOptJS in Compile in frontend).value.data)
    }.taskValue,
    watchSources ++= (watchSources in frontend).value
  )
  .enablePlugins(BuildInfoPlugin)
  .dependsOn(sharedJVM)

lazy val shared =
  (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure)).in(file("shared")).settings(
    scalaVersion := scalaV,
    libraryDependencies += "com.lihaoyi" %%% "upickle" % upickleV
  )

lazy val sharedJVM = shared.jvm
lazy val sharedJS = shared.js

def commonSettings = Seq(
  scalaVersion := scalaV,
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding",
    "UTF-8",
    "-language:higherKinds",
    "-language:postfixOps",
    "-feature",
    "-Xfatal-warnings"
  ),
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  buildInfoKeys := Seq[BuildInfoKey](name, version),
  buildInfoPackage := "com.chriskite.chatserver",
  turbo := true
)

   */

// The values of the Scala objects in ./project are available in build.sbt
scalaVersion in ThisBuild := ScalaConfig.version

// Set the version for all projects in this build
version in ThisBuild := BuildConfig.appVersion

lazy val root = project
  .in(file("."))
  .aggregate(client, server)
  .settings(
    name := s"${BuildConfig.appName} root"
  )

def inDevMode = sys.props.get("dev.mode").exists(value => value.equalsIgnoreCase("true"))

scalacOptions in ThisBuild := ScalaConfig.compilerOptions(inDevMode).value

// We can run this project by calling appJS/run and appJVM/run. Enter `projects` in SBT to see the name of all projects
lazy val app = crossProject(JSPlatform, JVMPlatform)
  .in(file("./app"))
  .settings(
    name := BuildConfig.appName,
    // The build info plugin writes these values into a BuildInfo object in the build info package
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "appbuildinfo",
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.10.3"),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1")
  )
  .jvmSettings(
    libraryDependencies ++= Dependencies.server.value,
    javaOptions in Universal ++= RuntimeConfig.debugOptions(inDevMode),
    javaOptions in Universal ++= RuntimeConfig.javaRuntimeOptions,
    // When running tests, we set credentials via this config instead of environment variables
    javaOptions in Test += s"-Dconfig.file=${sourceDirectory.value}/test/resources/application.test.conf",
    // We need to fork a JVM process when testing so the Java options above are applied
    fork in Test := true,
    // Don't generate ScalaDoc when we are in development mode
    publishArtifact in (Compile, packageDoc) := !inDevMode,
    publishArtifact in packageDoc := !inDevMode
    /*
      We enable JavaAppPackaging to create a jar. Also, this gives us access to the variables stage and executableScriptName.
      Issuing "appJVM/docker" in SBT creates a Docker image from that jar.
     */
  )
  .enablePlugins(JavaAppPackaging, BuildInfoPlugin)
  .jsSettings(
    libraryDependencies ++= Dependencies.client.scalaJsDependencies.value,
    // Regardless of whether we optimize the created JavaScript fast or fully, produce a .js file with the same name.
    // This way, we don't have to adapt the name of the script to load at the client when we switch optimize modes
    artifactPath in Compile in fastOptJS := (crossTarget in fastOptJS).value / ((moduleName in fastOptJS).value + ".js"),
    artifactPath in Compile in fullOptJS := (crossTarget in fullOptJS).value / ((moduleName in fullOptJS).value + ".js"),
    // SBT adds the JSApp of our application as the main method in the produced JavaScript
    scalaJSUseMainModuleInitializer := true
  )

/*
  We need to define the subprojects. Note that the names of these vals do not affect how you run the subprojects:
  It will be `<nameOfCrossProject>JS/run` and `<nameOfCrossProject>JVM/run`, irrespective of how these vals are named
   */
lazy val client = app.js.settings()

/*
  Adds the compiled JavaScript to the server's resources so the server can send the JavaScript to the client
  @return a sequence of files that consists of our generated JavaScript file. Wrapped in a setting task for SBT
   */
def addJavaScriptToServerResources() =
  if (inDevMode) {
    println("SBT for Scala.js example app is in dev mode")
    (resources in Compile) += (fastOptJS in (client, Compile)).value.data
  } else {
    println("SBT for Scala.js example app is in production mode")
    (resources in Compile) += (fullOptJS in (client, Compile)).value.data
  }

lazy val server = app.jvm.settings(
  addJavaScriptToServerResources()
)

