name := "scalaz-state-examples"

scalaVersion := "2.12.0"

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-explaintypes",
  "-Xlint"
)

libraryDependencies +=
  "org.scalaz" %% "scalaz-core" % "7.2.7"

