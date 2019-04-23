package neu


import kafka.serializer.StringDecoder
import org.ansj.recognition.impl.StopRecognition
import org.ansj.splitWord.analysis.DicAnalysis
import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Put}
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark._
import org.apache.spark.streaming._
import org.apache.spark.streaming.dstream._
import org.apache.spark.streaming.kafka.KafkaUtils


object ScalaKafkaStreaming {
  def main(args: Array[String]) {
    System.setProperty("hadoop.home.dir", "F:\\hadoop-2.7.6")

    val sparkConf = new SparkConf().setMaster("local[*]").setAppName("test")
    val scc = new StreamingContext(sparkConf, Duration(5000))
    scc.sparkContext.setLogLevel("ERROR")
    scc.checkpoint("C:\\Users\\Administrator\\Desktop\\spark_direct") // 因为使用到了updateStateByKey,所以必须要设置checkpoint
    val topics = Set("test") //我们需要消费的kafka数据的topic
    val brokers = "192.168.88.135:9092,192.168.88.132:9092,192.168.88.133:9092"
    val kafkaParam = Map[String, String](
      "zookeeper.connect" -> "192.168.88.135:2181,192.168.88.132:2181,192.168.88.133:2181",
      "metadata.broker.list" -> brokers, // kafka的broker list地址
      "serializer.class" -> "kafka.serializer.StringEncoder")

    val stream: InputDStream[(String, String)] = createStream(scc, kafkaParam, topics)

    val filter = new StopRecognition()
    filter.insertStopNatures("w")

    var rdds= stream
      .map(_._2)
      .map(x => x.split("!@#"))
      .map(x => (x(4) + " --- " + x(5) + " --- " +x(1)+ " --- " +x(2)+ " --- " +x(3),  DicAnalysis.parse(x(1)+" "+x(2)+" "+x(3)).recognition(filter).toStringWithOutNature(";;;")))
      .map(x => x._2.split(";;;").map(y => (y + "~" + x._1 , 1)))
      .flatMap(x => x)
      .reduceByKey(_+_)
      .map(x => (x._1.split("~")(0) , x._1.split("~")(1) + "~" + x._2)).reduceByKey( (x,y) => x + " #@! " + y )


    rdds.foreachRDD{ rdd=>
      rdd.foreachPartition {record=>

        val config = HBaseConfiguration.create
        //锟斤拷锟斤拷zookeeper锟斤拷群
        config.set("hbase.zookeeper.quorum", "192.168.88.135,192.168.88.132,192.168.88.133")
        config.set("hbase.zookeeper.property.clientPort", "2181")
        config.set("hbase.master", "192.168.88.128:16000")
        val connection = ConnectionFactory.createConnection(config)
        val table = connection.getTable(TableName.valueOf("test:bangumi"))
        for (line <- record) // Look for each record in the RDD
        {
          val put = new Put(Bytes.toBytes(line._1.toString))
          var keyword=line._1
          var information=line._2
          val get: Get = new Get(Bytes.toBytes(keyword))
          val result = table.get(get)
          var r = Bytes.toString(result.getValue(Bytes.toBytes("information"), Bytes.toBytes("information")))
          if(r==null) {
            put.addColumn(Bytes.toBytes("keyword"), Bytes.toBytes("keyword"), Bytes.toBytes(keyword))
            put.addColumn(Bytes.toBytes("information"), Bytes.toBytes("information"), Bytes.toBytes(information))
            table.put(put)
            println(line._1 + ":" + line.toString())
          }else{
            var list:Map[String,Int] = Map() //将hbase已经存在的数据取出来，装进list:Map[String,Int]
            var lit=r.split(" #@! ")
            for(a<-lit){
              list += (a.split("~")(0)->Integer.parseInt(a.split("~")(1)))
            }
            var ones=information.split(" #@! ")
            for(one<-ones){
              var url=one.split("~")(0)
              var num=Integer.parseInt(one.split("~")(1))
              var map2:Map[String,Int]=Map(one.split("~")(0)->Integer.parseInt(one.split("~")(1)))
              list = list ++ map2
            }
            //合并完毕后需要再从list取出来，装入hbase
            var info:String = ""
            var i:Int=0
            for(key<-list.keys) {
              if (i < list.size - 1) {
                info += key + "~" + list(key) + " #@! "
              }else{
                info += key + "~" + list(key)
              }
            }
            println(info)
            put.addColumn(Bytes.toBytes("keyword"), Bytes.toBytes("keyword"), Bytes.toBytes(keyword))
            put.addColumn(Bytes.toBytes("information"), Bytes.toBytes("information"), Bytes.toBytes(info))
            table.put(put)
          }
        }
        // 批量提交
        // 分区数据写入HBase后关闭连接
        connection.close()
      }
    }
    scc.start() // 真正启动程序
    scc.awaitTermination() //阻塞等待
  }

  val updateFunc = (currentValues: Seq[Int], preValue: Option[Int]) => {
    val curr = currentValues.sum
    val pre = preValue.getOrElse(0)
    Some(curr + pre)
  }
  /**
    * 创建一个从kafka获取数据的流.
    */

  def createStream(scc: StreamingContext, kafkaParam: Map[String, String], topics: Set[String]) = {
    KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](scc, kafkaParam, topics)
  }

}
