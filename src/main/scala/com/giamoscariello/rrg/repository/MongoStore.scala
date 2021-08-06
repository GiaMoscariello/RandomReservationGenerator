package com.giamoscariello.rrg.repository

import cats.effect.{IO, Resource, Sync}
import com.giamoscariello.rrg.model.{DataSample, DataSamples}
import com.mongodb.MongoClientSettings
import com.typesafe.config.{Config, ConfigFactory}
import org.bson.BsonDocument
import org.mongodb.scala.connection.ClusterSettings.Builder
import org.mongodb.scala.connection.{ClusterSettings, SocketSettings}
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.{Document, MongoClient, MongoCollection, MongoCredential, Observable, ServerAddress}
import shapeless.Lazy.apply

import java.util.concurrent.TimeUnit
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.util.Try

object MongoStore {

  def findAllDataSamples: IO[Future[List[Try[DataSample]]]] = {
    Resource.eval(IO.delay(ConfigFactory.load("private.conf")))
      .use {
        config: Config =>
          makeMongoClient(config)
            .flatMap {
              client: MongoClient =>
                IO {
                  val compute = ((for {
                    coll          <- client.getDatabase("RandomReservationGens").getCollection("DataSamples")
                    namesColl     <- findListOf(coll, "names")
                    surnamesColl  <- findListOf(coll, "surnames")
                    locationColl  <- findListOf(coll, "locations")
                    names = DataSamples.from(namesColl)
                    surnames = DataSamples.from(surnamesColl)
                    locations = DataSamples.from(locationColl)
                  } yield (List(names, surnames, locations))).value).head()
                  compute
                }
            }
      }
  }


  private def findListOf(coll: MongoCollection[Document], list: String)
  = coll.find[BsonDocument](equal("dataType", list))


  def makeMongoClient(config: Config)(implicit F: Sync[IO]): IO[MongoClient] =
    IO(
      createMongoClient(
        MongoConf(
          servers = config.getStringList("mongo-server-address").asScala.toList,
          port = config.getInt("mongo-server-port"),
          username = None,
          password = None,
          auth = config.getBoolean("mongo-auth-required")
        ))
    )


  private def createMongoClient(conf: MongoConf): MongoClient = {
    val builder = MongoClientSettings
      .builder()
      .applyToSocketSettings(ssb => ssb.applySettings(
        SocketSettings
          .builder()
          .readTimeout(5000, TimeUnit.SECONDS)
          .connectTimeout(5000, TimeUnit.SECONDS)
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
}
