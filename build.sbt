Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / watchBeforeCommand    := Watch.clearScreen
ThisBuild / watchTriggeredMessage := Watch.clearScreenOnTrigger

ThisBuild / scalaVersion := "3.1.2"
ThisBuild / organization := "fi.jpaju"
ThisBuild / version      := "1.0"

// To enable testcontainers to clean up created containers
Test / fork := true

ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-language:implicitConversions",
  "-unchecked",
  "-Xfatal-warnings",
  "-Ysafe-init",
  "-Ykind-projector",
  "-Wconf:id=E029:error,msg=non-initialized:error,msg=spezialized:error,cat=unchecked:error" // Pattern match exhaustivity etc.
) ++ Seq("-source", "future")

val kafkaVersion   = "3.2.0"
val logbackVersion = "1.2.11"

lazy val root =
  project
    .in(file("."))
    .settings(
      libraryDependencies ++= Seq(
        "org.apache.kafka"   % "kafka-clients"       % kafkaVersion,
        "org.apache.kafka"   % "kafka-streams"       % kafkaVersion,
        ("org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion).cross(CrossVersion.for3Use2_13),
        "ch.qos.logback"     % "logback-classic"     % logbackVersion
      )
    )
