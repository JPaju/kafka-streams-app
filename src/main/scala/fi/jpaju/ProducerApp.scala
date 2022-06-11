package fi.jpaju

import zio.*
import zio.stream.*
import zio.kafka.consumer.*
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde
import org.apache.kafka.clients.producer.ProducerRecord
import zio.kafka.serde.Serializer

object ProducerApp:

  lazy val run =
    (produceRandomSentence *> Random.nextIntBetween(500, 1500).flatMap(ms => ZIO.sleep(ms.millis))).forever

  val producerSettings = ProducerSettings(List(Config.BootstrapServers))

  val producerLayer: ZLayer[Any, Throwable, Producer] =
    ZLayer.scoped(Producer.make(producerSettings))

  private lazy val produceRandomSentence: ZIO[Random & Producer, Throwable, Unit] =
    for
      sentence <- randomSentence
      record = new ProducerRecord[String, String](Topics.Sentences, sentence)
      _ <- Producer.produce(record, Serde.string, Serde.string).debug(s"Produced sentence: $sentence")
    yield ()

  private lazy val randomSentence: ZIO[Random, Nothing, String] =
    Random.nextIntBetween(0, sentences.size).map(sentences)

  private lazy val sentences = Vector(
    "As the asteroid hurtled toward earth, Becky was upset her dentist appointment had been canceled.",
    "We should play with legos at camp.",
    "The small white buoys marked the location of hundreds of crab pots.",
    "The wake behind the boat told of the past while the open sea for told life in the unknown future.",
    "His ultimate dream fantasy consisted of being content and sleeping eight hours in a row.",
    "Separation anxiety is what happens when you can't find your phone.",
    "He strives to keep the best lawn in the neighborhood.",
    "A dead duck doesn't fly backward.",
    "This book is sure to liquefy your brain.",
    "Getting up at dawn is for the birds.",
    "A suit of armor provides excellent sun protection on hot days.",
    "Charles ate the french fries knowing they would be his last meal.",
    "It was difficult for Mary to admit that most of her workout consisted of exercising poor judgment.",
    "She wasn't sure whether to be impressed or concerned that he folded underwear in neat little packages.",
    "She did her best to help him.",
    "He would only survive if he kept the fire going and he could hear thunder in the distance.",
    "Truth in advertising and dinosaurs with skateboards have much in common.",
    "Today arrived with a crash of my car through the garage door.",
    "The underground bunker was filled with chips and candy.",
    "He was disappointed when he found the beach to be so sandy and the sun so sunny.",
    "Bill ran from the giraffe toward the dolphin.",
    "I met an interesting turtle while the song on the radio blasted away.",
    "The elderly neighborhood became enraged over the coyotes who had been blamed for the poodle’s disappearance.",
    "I am never at home on Sundays.",
    "The blinking lights of the antenna tower came into focus just as I heard a loud snap.",
    "I became paranoid that the school of jellyfish was spying on me.",
    "Jason didn’t understand why his parents wouldn’t let him sell his little sister at the garage sale.",
    "Peanut butter and jelly caused the elderly lady to think about her past.",
    "It's never comforting to know that your fate depends on something as unpredictable as the popping of corn.",
    "The elephant didn't want to talk about the person in the room.",
    "He walked into the basement with the horror movie from the night before playing in his head.",
    "Nancy was proud that she ran a tight shipwreck.",
    "His ultimate dream fantasy consisted of being content and sleeping eight hours in a row.",
    "His get rich quick scheme was to grow a cactus farm.",
    "She had that tint of craziness in her soul that made her believe she could actually make a difference.",
    "When nobody is around, the trees gossip about the people who have walked under them.",
    "The memory we used to share is no longer coherent.",
    "Someone I know recently combined Maple Syrup & buttered Popcorn thinking it would taste like caramel popcorn. It didn’t and they don’t recommend anyone else do it either.",
    "They finished building the road they knew no one would ever use.",
    "Instead of a bachelorette party"
  )
