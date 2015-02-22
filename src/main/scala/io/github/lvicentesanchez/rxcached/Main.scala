package io.github.lvicentesanchez.rxcached

import akka.actor.ActorSystem
import akka.stream.{ ActorFlowMaterializer, FlowMaterializer }
import akka.stream.scaladsl._
import akka.util.ByteString
import java.net.InetSocketAddress
import scala.concurrent.ExecutionContext

object Main extends App {

  implicit val system: ActorSystem = ActorSystem("rx-cache")
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val materializer: FlowMaterializer = ActorFlowMaterializer()

  val connection: StreamTcp.OutgoingConnection = StreamTcp().outgoingConnection(new InetSocketAddress("localhost", 11211))

  Source(List(
    ByteString("add key1 0 0 6\r\nvalue4\r\n"),
    ByteString("get key1\r\n"),
    ByteString("stats items\r\n")
  )).
    via(connection.flow).
    runWith(Sink.foreach(str => println(str.decodeString("UTF-8")))).
    onComplete {
      _ =>
        system.shutdown()
        system.awaitTermination()
    }
}
