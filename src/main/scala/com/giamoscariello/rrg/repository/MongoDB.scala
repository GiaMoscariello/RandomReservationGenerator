package com.giamoscariello.rrg.repository

import com.giamoscariello.rrg.model.{DataSample, DataSamples}
import com.mongodb.MongoClientSettings
import com.typesafe.config.ConfigFactory
import org.bson.BsonDocument
import org.mongodb.scala.connection.ClusterSettings.Builder
import org.mongodb.scala.connection.{ClusterSettings, SocketSettings}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{MongoClient, MongoCollection, MongoCredential, ServerAddress}

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.util.Try

object MongoDB {

  def make: MongoDB =
    MongoDB(createMongoClient
      .getDatabase("RandomReservationGens")
      .getCollection[BsonDocument]("DataSamples")
    )

  private def createMongoClient: MongoClient = {
    val conf = makeConf

    val builder = MongoClientSettings
      .builder()
      .applyToSocketSettings(ssb => ssb.applySettings(
        SocketSettings
          .builder()
          .readTimeout(50000, TimeUnit.SECONDS)
          .connectTimeout(50000, TimeUnit.SECONDS)
          .build
      ))
      .applyToClusterSettings((csb: Builder) => {
        csb.applySettings(
          ClusterSettings
            .builder()
            .hosts(conf.servers.map(addr => ServerAddress(host = addr, port = conf.port)).asJava)
            .build()
        )
      })
    if (conf.auth)
      builder.credential(
        MongoCredential.createScramSha1Credential(
          userName = conf.username.get,
          password = conf.password.get.toCharArray,
          source = "admin"
        )
      ).build()
    MongoClient(builder.build())
  }

  private def makeConf: MongoConf = {
    val config = ConfigFactory.load("private.conf")

    MongoConf(
      servers = config.getStringList("mongo-server-address").asScala.toList,
      port = config.getInt("mongo-server-port"),
      username = None,
      password = None,
      auth = config.getBoolean("mongo-auth-required")
    )
  }
}

case class MongoDB(collection: MongoCollection[BsonDocument]) {
  def getDataSampleBy(filter: String): Future[Try[DataSample]] = collection
    .find[BsonDocument](equal("dataType", filter))
    .head()
    .map(doc => DataSamples.from(doc))
}
