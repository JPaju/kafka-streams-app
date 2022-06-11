package fi.jpaju

import java.util.Properties

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.streams.{StreamsConfig, StreamsBuilder, KafkaStreams, Topology}
import org.apache.kafka.streams.kstream

object WordCountTopology:
  def add(builder: StreamsBuilder): Unit =

    val sentences: KStream[String, String] = builder.stream[String, String](Topics.Sentences)
    val words: KStream[String, String]     = builder.stream(Topics.Words)

    sentences
      .flatMapValues(wordsFromSentence)
      .to(Topics.Words)

    sentences.foreach((key, value) => println(s"[SENTENCES]: \t key: $key, value: $value"))
    words.foreach((key, value) => println(s"[WORDS]: \t key: $key, value: $value"))

  private def wordsFromSentence(sentence: String): List[String] =
    sentence
      .split(" ")
      .map(_.toLowerCase)
      .map(_.filter(_.isLetter))
      .toList

end WordCountTopology

object StreamsApp:
  def run(): Unit =
    val topology = createTopology
    val props    = createProperties

    println(topology.describe())

    val streams = new KafkaStreams(topology, props)
    addShutdownHook(streams)
    streams.start()

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

  private def addShutdownHook(streams: KafkaStreams): Unit =
    Runtime
      .getRuntime()
      .addShutdownHook(
        new Thread(() =>
          println("Shutting down streams app")
          streams.close()
        )
      )
  end addShutdownHook
