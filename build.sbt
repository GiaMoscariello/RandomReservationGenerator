name := "RandomReservationGenerator"

version := "0.1"

scalaVersion := "2.13.6"
resolvers += "Confluent Repo" at "https://packages.confluent.io/maven"
resolvers += "Artifactory" at "https://kaluza.jfrog.io/artifactory/maven"

libraryDependencies += "org.typelevel" %% "cats-core" % "2.6.1"
libraryDependencies += "org.typelevel" %% "cats-effect" % "2.5.1"
libraryDependencies += "org.typelevel" %% "discipline-scalatest" % "2.1.5" % Test
libraryDependencies += "org.mongodb.scala" %% "mongo-scala-driver" % "4.3.0"
libraryDependencies += "com.typesafe" % "config" % "1.4.1"
libraryDependencies += "joda-time" % "joda-time" % "2.10.10"
libraryDependencies += "io.confluent" % "kafka-avro-serializer" % "6.2.0"
libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-kafka" % "4.0.10"
libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-json" % "4.0.10"
libraryDependencies += "com.sksamuel.avro4s" %% "avro4s-core" % "4.0.10"
libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.32"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies ++= Seq(
  "org.apache.kafka" %% "kafka" % "2.8.0",
  "org.apache.kafka" % "kafka-clients" % "2.8.0",
  "org.apache.kafka" % "kafka-streams" % "2.8.0",
  "org.apache.kafka" %% "kafka-streams-scala" % "2.8.0",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1"
)
//libraryDependencies ++= {
//  val kafkaSerializationV = "0.5.25"
//  Seq(
//    "com.ovoenergy" %% "kafka-serialization-core" % kafkaSerializationV,
//    "com.ovoenergy" %% "kafka-serialization-circe" % kafkaSerializationV, // To provide Circe JSON support
//    "com.ovoenergy" %% "kafka-serialization-json4s" % kafkaSerializationV, // To provide Json4s JSON support
//    "com.ovoenergy" %% "kafka-serialization-jsoniter-scala" % kafkaSerializationV, // To provide Jsoniter Scala JSON support
//    "com.ovoenergy" %% "kafka-serialization-spray" % kafkaSerializationV, // To provide Spray-json JSON support
//    "com.ovoenergy" %% "kafka-serialization-avro4s" % kafkaSerializationV // To provide Avro4s Avro support
//  )
//}