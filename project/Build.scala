import sbt._

object Builds extends sbt.Build {
  import Keys._
  import sbtrelease.ReleasePlugin._
  import sbtbuildinfo.Plugin._

  lazy val buildSettings = Defaults.defaultSettings ++ releaseSettings ++ Seq(
    organization := "net.rosien",
    scalaVersion := "2.10.3",
    scalacOptions ++= Seq("-deprecation", "-unchecked"),
    publishArtifact in Test := false,
    publishMavenStyle := true,
    publishTo <<= version { v: String =>
      val nexus = "https://oss.sonatype.org/"
      if (v.trim.endsWith("SNAPSHOT"))
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases" at nexus + "service/local/staging/deploy/maven2")
    },
    pomExtra := (
      <url>https://github.com/arosien/configz</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:arosien/configz.git</url>
        <connection>scm:git:git@github.com:arosien/configz.git</connection>
      </scm>
      <developers>
        <developer>
          <id>arosien</id>
          <name>Adam Rosien</name>
          <url>http://rosien.net</url>
        </developer>
      </developers>),
    initialCommands in console := """
      |import com.typesafe.config._
      |import net.rosien.configz._
      |import scalaz._
      |import Scalaz._
      |""".stripMargin
  )

  // Depends on 'core' so that one can depend on 'configz' or 'configz-core'.
  lazy val root = Project("configz", file("."),
    settings = buildSettings ++ Seq(
      name := "configz",
      description := "configz"
    )) aggregate(core) dependsOn(core)

  val scalazVersion = "7.0.4"

  lazy val core = Project("configz-core", file("core"),
    settings = buildSettings ++ buildInfoSettings ++ Seq(
      description := "configz",
      sourceGenerators in Compile <+= buildInfo,
      buildInfoPackage := "net.rosien.configz",
      libraryDependencies ++= Seq(
        "com.typesafe"   % "config"                     % "1.0.2",
        "org.scalaz"    %% "scalaz-core"                % scalazVersion,
        "org.scalaz"    %% "scalaz-scalacheck-binding"  % scalazVersion,
        "org.typelevel" %% "scalaz-specs2"              % "0.1.5"  % "test"
      )
    ))
}
