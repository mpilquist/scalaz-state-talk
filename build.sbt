name := "scalaz-state-examples"

scalaVersion := "2.9.2"

scalacOptions ++= Seq(
  "-deprecation",
  "-unchecked",
  "-optimise",
  "-explaintypes",
  "-Xcheckinit",
  "-Xfatal-warnings",
  "-Xlint",
  "-Xverify",
  "-Yclosure-elim",
  "-Ydead-code",
  "-Yinline",
  "-Ywarn-all"
)

triggeredMessage := (_ => Watched.clearScreen)

libraryDependencies ++=
  "org.scalaz" %% "scalaz-core" % "7.0.0-M3" ::
  "org.scalaz" %% "scalaz-effect" % "7.0.0-M3" ::
  "org.scalaz" %% "scalaz-iteratee" % "7.0.0-M3" ::
  "org.scalatest" % "scalatest_2.9.0" % "1.8" % "test" ::
  Nil

