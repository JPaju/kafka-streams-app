package fi.jpaju

import zio.*

object Main extends ZIOAppDefault:
  val apps = Seq(
    ProducerApp.run,
    ConsumerApp.run
  )

  val program = for
    _ <- ZIO.debug("Starting program")
    _ <- ZIO.raceAll(StreamsApp.run, apps)
    _ <- ZIO.debug("Stopping program")
  yield ()

  val run =
    program.provide(
      ProducerApp.producerLayer,
      ConsumerApp.consumerLayer,
      Random.live
    )
