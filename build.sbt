name := "Copitte"

version := "0.0.1"

scalaVersion := "2.9.1"

seq(webSettings :_*)

libraryDependencies ++= Seq(
  "javax.servlet" % "servlet-api" % "2.5" % "provided",
  "org.mortbay.jetty" % "jetty" % "6.1.22" % "container",
  "org.apache.wink" % "wink-server" % "1.1.2-incubating",
  "org.apache.wink" % "wink" % "1.1.2-incubating",
  "org.apache.wink" % "wink-client" % "1.1.2-incubating",
  "org.scalatest" %% "scalatest" % "1.7.2" % "test",
  "net.debasishg" %% "sjson" % "0.17",
  "org.slf4j" % "slf4j-api" %  "1.6.4",
  "org.slf4j" % "slf4j-simple" %  "1.6.4"
)
