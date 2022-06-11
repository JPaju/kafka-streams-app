import sbt._

object CompatibilityUtils {
  // Ensures that transitive dependencies to Scala 2 org.scala-lang:modules are not included
  def use2_13ExcludeScalaModules(module: ModuleID): ModuleID =
    module
      .cross(CrossVersion.for3Use2_13)
      .excludeAll(ExclusionRule(organization = "org.scala-lang.modules"))
}
