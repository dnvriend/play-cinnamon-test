name := "play-cinnamon-test"

organization := "com.github.dnvriend"

version := "1.0.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.17"

// akka
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence" % akkaVersion
libraryDependencies += "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion

// fp
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.9"
libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1." % Test
libraryDependencies += "com.github.mpilquist" %% "simulacrum" % "0.10.0"

// test
libraryDependencies += "org.typelevel" %% "scalaz-scalatest" % "1.1.1" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.6.8" % Test
libraryDependencies += "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "2.0.0-M2" % Test

// ws
libraryDependencies += ws
libraryDependencies += "com.github.nscala-time" %% "nscala-time" % "2.14.0"
libraryDependencies += "com.typesafe.play" %% "play-json" % "2.5.10"

fork in Test := true

parallelExecution := false

enablePlugins(PlayScala)

// ============================================
// ==== buildinfo (Information about the Build)
// ============================================
enablePlugins(BuildInfoPlugin)

buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion)

buildInfoOptions += BuildInfoOption.ToMap

buildInfoOptions += BuildInfoOption.ToJson

buildInfoOptions += BuildInfoOption.BuildTime

buildInfoPackage := organization.value


// ==================================
// ==== scalariform (Code Formatting)
// ==================================
import scalariform.formatter.preferences._
import com.typesafe.sbt.SbtScalariform
import sbt.complete.DefaultParsers

SbtScalariform.autoImport.scalariformPreferences := SbtScalariform.autoImport.scalariformPreferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(AlignSingleLineCaseStatements.MaxArrowIndent, 100)
  .setPreference(DoubleIndentClassDeclaration, true)    


// ====================================
// ==== Lightbend Monitoring (Cinnamon)
// ====================================
// Enable the Cinnamon Lightbend Monitoring sbt plugin
enablePlugins (Cinnamon)

libraryDependencies += Cinnamon.library.cinnamonSandbox

// Add the Monitoring Agent for run and test
cinnamon in run := true
cinnamon in test := true

// Use Mapped Diagnostic Context for adding extra identifiers to log messages.
//libraryDependencies += Cinnamon.library.cinnamonSlf4jMdc

// Use Coda Hale Metrics
// http://metrics.dropwizard.io/3.1.0/
//libraryDependencies += Cinnamon.library.cinnamonCHMetrics
  
// Use Akka instrumentation
//libraryDependencies += Cinnamon.library.cinnamonAkka

// =======================================
// ==== Lightbend Orchestration (ConductR)
// =======================================
// read: https://github.com/typesafehub/conductr-lib#play25-conductr-bundle-lib
// =======================================
enablePlugins(PlayBundlePlugin)

// Declares endpoints. The default is Map("web" -> Endpoint("http", 0, Set.empty)).
// The endpoint key is used to form a set of environment variables for your components,
// e.g. for the endpoint key "web" ConductR creates the environment variable WEB_BIND_PORT.
BundleKeys.endpoints := Map(
  "play" -> Endpoint(bindProtocol = "http", bindPort = 0, services = Set(URI("http://:9000/play"))),
  "akka-remote" -> Endpoint("tcp")
)

normalizedName in Bundle := "play-cinnamon-test" // the human readable name for your bundle

BundleKeys.system := "HelloPlaySystem" // represents the clustered ActorSystem

BundleKeys.startCommand += "-Dhttp.address=$PLAY_BIND_IP -Dhttp.port=$PLAY_BIND_PORT"

// =====================================================
// ==== sbt-header (Headers for source and config files)
// =====================================================
enablePlugins(AutomateHeaderPlugin)

licenses +=("Apache-2.0", url("http://opensource.org/licenses/apache2.0.php"))

import de.heikoseeberger.sbtheader.license.Apache2_0

headers := Map(
  "scala" -> Apache2_0("2016", "Dennis Vriend"),
  "conf" -> Apache2_0("2016", "Dennis Vriend", "#")
)

// ==============
//

val actorUrl = settingKey[String]("Actor Service URL")
actorUrl := "192.168.10.1:9000/play/api/actor"

val callActor = inputKey[String]("Calling Actor Service")

callActor := {
  import scala.concurrent._
  import scala.concurrent.blocking
  import scala.concurrent.duration._
  import scala.concurrent.ExecutionContext.Implicits.global
  val logger = streams.value.log
  def callUrl(x: Int): Future[String] = Future {
    val cmd = s"http GET ${actorUrl.value}"
    logger.info(s"[$x] Executing '$cmd'")
    val resp = blocking(Process(cmd).lines.mkString)
    logger.info(s"[$x] $resp")
    resp
  }

  val numberOfTimes: Int = (DefaultParsers.Space.? ~> (DefaultParsers.IntBasic ?? 1)).parsed
  val f = for {
    x <- 1 to numberOfTimes
  } yield callUrl(x)

  Await.result(Future.sequence(f), 60.seconds).mkString
}