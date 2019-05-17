import com.typesafe.config.ConfigFactory
import kafka.consumers.{BaseConsumer, OrderConsumer, ShippingConsumer, ShippingStatusConsumer}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ArrayBuffer

object App {
  def main(args: Array[String]): Unit = {
    val conf = ConfigFactory.load
    val sparkConf = new SparkConf()
      .setAppName("OrderStreamingFromRDD")
      .setMaster("local[*]")

    sparkConf.set("es.index.auto.create", "true")
    sparkConf.set("es.mapping.id", "id")
    val sc = new SparkContext(sparkConf)

    // streams will produce data every second
    val ssc = new StreamingContext(sc, Seconds(1))

    val consumers: ArrayBuffer[BaseConsumer] = new ArrayBuffer[BaseConsumer]()
    consumers += new OrderConsumer(conf,ssc)
    consumers += new ShippingConsumer(conf,ssc)
    consumers += new ShippingStatusConsumer(conf,ssc)

    ssc.start()
    var input: String = ""
    do {
      input = scala.io.StdIn.readLine("[press q to quite]")
    } while (!input.equals("q"))

    ssc.stop()
  }
}