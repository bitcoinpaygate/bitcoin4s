import org.scalafmt.sbt.ScalafmtPlugin._

import scala.io.Source

name := "bitcoin4s"

version := Source.fromFile("VERSION").getLines.mkString

scalaVersion := "2.12.10"

organization := "bitcoinpaygate"

libraryDependencies ++= {
  val sttpVersion = "1.6.0"
  val akkaVersion = "2.5.25"
  val scalaTestVersion = "3.0.5"
  val sprayJsonVersion = "1.3.5"

  Seq(
    "org.scalatest"         %% "scalatest"         % scalaTestVersion % "test,it",
    "io.spray"              %% "spray-json"        % sprayJsonVersion,
    "com.softwaremill.sttp" %% "core"              % sttpVersion,
    "com.softwaremill.sttp" %% "akka-http-backend" % sttpVersion,
    "com.typesafe.akka"     %% "akka-stream"       % akkaVersion % "provided,test,it"
  )
}

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

scalafmtOnCompile := true

lazy val IntegrationTest = config("it") extend Test

lazy val root = (project in file("."))
  .configs(IntegrationTest)
  .settings(
    Defaults.itSettings,
    inConfig(IntegrationTest)(scalafmtConfigSettings)
  )

addCommandAlias("testAll", ";test")
addCommandAlias("formatAll", ";scalafmtAll;test:scalafmtAll;scalafmtSbt")
addCommandAlias("compileAll", ";compile;test:compile")
addCommandAlias("checkFormatAll", ";scalafmtCheckAll;scalafmtSbtCheck")
