import CompatibilityUtils._

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

val kafkaVersion    = "3.2.0"
val logbackVersion  = "1.2.11"
val zioVersion      = "2.0.0-RC5"
val zioKafkaVersion = "2.0.0-M3"

val javaDependencies = Seq(
  "org.apache.kafka" % "kafka-clients"   % kafkaVersion,
  "org.apache.kafka" % "kafka-streams"   % kafkaVersion,
  "ch.qos.logback"   % "logback-classic" % logbackVersion
)

val scalaDependencies = Seq(
  use2_13ExcludeScalaModules("org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion),
  "dev.zio" %% "zio-test"  % zioVersion,
  "dev.zio" %% "zio-kafka" % zioKafkaVersion
)

lazy val root =
  project
    .in(file("."))
    .settings(
      libraryDependencies ++= javaDependencies ++ scalaDependencies
    )
