import com.typesafe.sbt.SbtScalariform
import com.typesafe.sbt.SbtScalariform.ScalariformKeys

import scalariform.formatter.preferences._

name := "bitcoin4s"

version := "0.3.0"

scalaVersion := "2.12.4"

crossScalaVersions := Seq("2.12.4")

organization := "bitcoinpaygate"

libraryDependencies ++= {
  val akkaHttpVersion       = "10.0.5"

  Seq(
    "org.scalatest"        %% "scalatest"              % "3.0.1"          % "test",
    "com.typesafe.akka"    %% "akka-http-core"         % akkaHttpVersion,
    "com.typesafe.akka"    %% "akka-http-spray-json"   % akkaHttpVersion,
    "io.spray"             %% "spray-json"             % "1.3.3"
  )
}

scalacOptions ++= Seq(
  "-encoding", "utf8",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-unchecked",
  "-deprecation",
  "-Xfatal-warnings",
  "-Xlint",
  "-Ywarn-dead-code"
)

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
        <url>https://github.com/wlk/bitcoin4s</url>
        <connection>https://github.com/wlk/bitcoin4s</connection>
      </scm>,
      publishArtifact in Test := false,
      homepage := Some(url("https://github.com/wlk/bitcoin4s")),
      publishMavenStyle := false
    )
