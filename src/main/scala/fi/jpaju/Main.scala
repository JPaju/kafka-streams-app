package fi.jpaju

import zio.*

object Main extends ZIOAppDefault:
  val program = for
    _ <- ZIO.debug("Starting program")
    _ <- ProducerApp.run
    _ <- ZIO.debug("Stopping program")
  yield ()

  val run =
    program.provide(ProducerApp.producerLayer, Random.live)
