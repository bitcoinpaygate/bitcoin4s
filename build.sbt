import scala.io.Source

name := "bitcoin4s"

version := Source.fromFile("VERSION").getLines.mkString

scalaVersion := "2.12.8"

organization := "bitcoinpaygate"

libraryDependencies ++= {
  val sttpVersion = "1.3.0"
  val akkaVersion = "2.5.16"
  val scalaTestVersion = "3.0.5"
  val sprayJsonVersion = "1.3.4"

  Seq(
    "org.scalatest"         %% "scalatest"         % scalaTestVersion % "test",
    "io.spray"              %% "spray-json"        % sprayJsonVersion,
    "com.softwaremill.sttp" %% "core"              % sttpVersion,
    "com.softwaremill.sttp" %% "akka-http-backend" % sttpVersion,
    "com.typesafe.akka"     %% "akka-stream"       % akkaVersion % "provided"
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

addCommandAlias("testAll", ";test")
addCommandAlias("formatAll", ";scalafmt;test:scalafmt;scalafmtSbt")
addCommandAlias("compileAll", ";compile;test:compile")
