import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys
import scalariform.formatter.preferences._

import scala.io.Source

name := "bitcoin4s"

version := Source.fromFile("VERSION").getLines.mkString

scalaVersion := "2.12.6"

organization := "bitcoinpaygate"

libraryDependencies ++= {
  val akkaHttpVersion       = "10.0.13"
  val scalaTestVersion      = "3.0.5"
  val sprayJsonVersion      = "1.3.4"

  Seq(
    "org.scalatest"        %% "scalatest"              % scalaTestVersion          % "test",
    "com.typesafe.akka"    %% "akka-http-core"         % akkaHttpVersion,
    "com.typesafe.akka"    %% "akka-http-spray-json"   % akkaHttpVersion,
    "io.spray"             %% "spray-json"             % sprayJsonVersion
  )
}

scalacOptions --= Seq("-Xfatal-warnings")

scalariformAutoformat := true
ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(AlignSingleLineCaseStatements, true)
  .setPreference(DoubleIndentConstructorArguments, true)
  .setPreference(SpacesAroundMultiImports, false)
  .setPreference(CompactControlReadability, false)

bintrayOrganization := Some("bitcoinpaygate")

bintrayRepository := "bitcoinpaygate-maven"

licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))

val doNotPublishSettings = Seq(publish := {})

val publishSettings =
  if (version.toString.endsWith("-SNAPSHOT"))
    Seq(
      publishTo := Some("Artifactory Realm" at "http://oss.jfrog.org/artifactory/oss-snapshot-local"),
      bintrayReleaseOnPublish := false,
      credentials := List(Path.userHome / ".bintray" / ".artifactory").filter(_.exists).map(Credentials(_))
    )
  else
    Seq(
      pomExtra := <scm>
        <url>https://github.com/bitcoinpaygate/bitcoin4s</url>
        <connection>https://github.com/bitcoinpaygate/bitcoin4s</connection>
      </scm>,
      publishArtifact in Test := false,
      homepage := Some(url("https://github.com/bitcoinpaygate/bitcoin4s")),
      publishMavenStyle := false
    )
