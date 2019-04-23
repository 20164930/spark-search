package neu

import java.util.Properties

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object KafkaRead{
  def run(args: String): Unit = {
    val BROKER_LIST = "192.168.88.135:9092,192.168.88.132:9092,192.168.88.133:9092"

    /**
      * 1、配置属性
      * metadata.broker.list : kafka集群的broker
      * serializer.class : 如何序列化发送消息
      * request.required.acks : 1代表需要broker接收到消息后acknowledgment,默认是0
      * producer.type : async/sync 默认就是同步sync
      */
    val props = new Properties()
    props.put("bootstrap.servers", BROKER_LIST)
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("request.required.acks", "1")
    props.put("producer.type", "async")

    val producer = new KafkaProducer[String, String](props)
    try {
      val record = new ProducerRecord[String, String]("test",args)

      println( record)
      producer.send(record)

      try {
        Thread.sleep(100)
      } catch {
        case e: Exception => println(e)
      }


    } catch {
      case e: Exception => println(e)
    }
  }

}