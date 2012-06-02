name := "Copitte"

version := "0.0.1"

scalaVersion := "2.9.1"

artifactName := { (config: String, module: ModuleID, artifact: Artifact) =>
  artifact.name + "." + artifact.extension
}

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
  "org.apache.wink" % "wink-server" % "1.1.2-incubating",
  "org.apache.wink" % "wink" % "1.1.2-incubating",
  "org.apache.wink" % "wink-client" % "1.1.2-incubating",
  "org.scalatest" %% "scalatest" % "1.7.2" % "test",
  "org.slf4j" % "slf4j-api" %  "1.6.4",
  "org.slf4j" % "slf4j-simple" %  "1.6.4",
  "net.liftweb" %% "lift-json" % "2.4",
  "commons-io" % "commons-io" % "2.3",
  "org.fusesource.scalate" % "scalate-core" % "1.5.3"
)
