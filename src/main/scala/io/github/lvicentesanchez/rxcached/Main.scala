package io.github.lvicentesanchez.rxcached

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.stream.{ ActorFlowMaterializer, FlowMaterializer }
import akka.stream.scaladsl._
import akka.util.ByteString
import scala.concurrent.ExecutionContext

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("rx-cache")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val materializer: FlowMaterializer = ActorFlowMaterializer()

  val connection: StreamTcp.OutgoingConnection = StreamTcp().outgoingConnection(new InetSocketAddress("localhost", 11211))

  Source.single(ByteString("add key1 0 0 6\r\nvalue4\r\nget key1\r\nstats items\r\n")).
    via(connection.flow).
    runWith(Sink.foreach(str => println(str.decodeString("UTF-8")))).
    onComplete {
      _ =>
        system.shutdown()
        system.awaitTermination()
    }
}
