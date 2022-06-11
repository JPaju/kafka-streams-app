package fi.jpaju

import zio.*

object Main extends ZIOAppDefault:
  val timer = ZIO.sleep(15.seconds) *> ZIO.debug("Timer expired!!")

  val apps = Seq(
    StreamsApp.run,
    ProducerApp.run,
    ConsumerApp.run
  )

  val program = for
    _ <- ZIO.debug("Starting program")
    _ <- ZIO.raceAll(timer, apps)
    _ <- ZIO.debug("Stopping program")
  yield ()

  val run =
    program.provide(
      ProducerApp.producerLayer,
      ConsumerApp.consumerLayer,
      Random.live
    )
