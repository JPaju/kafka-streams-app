package fi.jpaju

import java.util.Properties

import zio.*

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.{StreamsConfig, StreamsBuilder, KafkaStreams, Topology}

object WordCountTopology:
  def add(builder: StreamsBuilder): Unit =

    val sentences: KStream[String, String] = builder.stream[String, String](Topics.Sentences)
    val words: KStream[String, String]     = builder.stream(Topics.Words)

    sentences
      .flatMapValues(wordsFromSentence)
      .to(Topics.Words)

  private def wordsFromSentence(sentence: String): List[String] =
    sentence
      .split(" ")
      .map(_.toLowerCase)
      .map(_.filter(_.isLetter))
      .toList

end WordCountTopology

object StreamsApp:

  def run: Task[Nothing] =
    ZIO.scoped {
      scopedApp.flatMap(app =>
        ZIO.attempt(app.start()) *>
          ZIO.debug("Streams app started") *>
          ZIO.never
      )
    }

  private def scopedApp: ZIO[Scope, Throwable, KafkaStreams] =
    ZIO.acquireRelease(
      streamsApp
    )(streamsApp => ZIO.debug("Shutting down streams app") *> ZIO.attempt(streamsApp.close()).orDie)

  private def streamsApp: Task[KafkaStreams] = ZIO.attempt {
    val topology = createTopology
    val props    = createProperties

    println(topology.describe())

    new KafkaStreams(topology, props)
  }

  private def createProperties: Properties =
    val props = new Properties()
    props.put(StreamsConfig.APPLICATION_ID_CONFIG, Config.StreamsAppName)
    props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, Config.BootstrapServers)
    props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass)
    props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.stringSerde.getClass)
    props
  end createProperties

  private def createTopology: Topology =
    val builder = new StreamsBuilder

    WordCountTopology.add(builder)

    builder.build()
  end createTopology
