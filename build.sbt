ThisBuild / scalaVersion := "2.13.11"
ThisBuild / organization := "com.alejandrohdezma"

addCommandAlias("ci-test", "fix --check; mdoc; test; publishLocal;")
addCommandAlias("ci-docs", "github; headerCreateAll; mdoc")
addCommandAlias("ci-publish", "github; ci-release")

lazy val documentation = project
  .enablePlugins(MdocPlugin)
  .settings(mdocOut := file("."))
  .dependsOn(`tapir-golden-openapi-munit`)
  .dependsOn(`tapir-golden-openapi-munit-validator`)

lazy val `tapir-golden-openapi-munit` = module
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-core" % "1.6.4")
  .settings(libraryDependencies += "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs" % "1.6.1")
  .settings(libraryDependencies += "com.softwaremill.sttp.apispec" %% "openapi-circe-yaml" % "0.6.0")
  .settings(libraryDependencies += "org.scalameta" %% "munit" % "0.7.29")

lazy val `tapir-golden-openapi-munit-validator` = module
  .dependsOn(`tapir-golden-openapi-munit`)
  .settings(libraryDependencies += "io.swagger.parser.v3" % "swagger-parser" % "2.1.16")
