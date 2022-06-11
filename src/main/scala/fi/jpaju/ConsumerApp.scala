package fi.jpaju

import zio.*
import zio.kafka.*
import zio.kafka.serde.Serde
import zio.kafka.consumer.{Subscription, ConsumerSettings, Consumer}
import zio.kafka.consumer.CommittableRecord
import zio.stream.ZStream

object ConsumerApp:
  private val consumerSettings =
    ConsumerSettings(List(Config.BootstrapServers))
      .withGroupId(Config.ConsumerAppName)

  private val consumer = Consumer.make(consumerSettings)
  val consumerLayer    = ZLayer.scoped(consumer)

  private val subscription = Subscription.topics(Topics.CommonWordCounts)

  val run: ZIO[Consumer, Throwable, Unit] =
    Consumer
      .subscribeAnd(subscription)
      .plainStream(Serde.string, Serde.long)
      .tap(record => ZIO.debug(s"[Consumer] Word: ${record.key}, count: ${record.value}"))
      .map(_.offset)
      .aggregateAsync(Consumer.offsetBatches)
      .mapZIO(_.commit)
      .runDrain
