package neu

import org.ansj.splitWord.analysis.DicAnalysis
import org.ansj.recognition.impl.StopRecognition
import org.apache.hadoop.hbase.client.{ConnectionFactory, Get, Put, Scan}
import org.apache.hadoop.hbase.filter._
import java.util

import org.apache.hadoop.hbase.{HBaseConfiguration, TableName}
import org.apache.hadoop.hbase.client._
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.{Base64, Bytes}
import org.apache.spark.{SparkConf, SparkContext}

import scala.collection.JavaConverters._

object Findword{

  def run(args: String): java.util.Map[String,Int] = {

    val config = HBaseConfiguration.create
    config.set("hbase.zookeeper.quorum", "192.168.88.135,192.168.88.132,192.168.88.133")
    config.set("hbase.zookeeper.property.clientPort", "2181")
    config.set("hbase.master", "192.168.88.128:16000")
    val connection = ConnectionFactory.createConnection(config)
    val filter = new StopRecognition()
    filter.insertStopNatures("w")
    var k=args
    val w=DicAnalysis.parse(k).recognition(filter).toStringWithOutNature(";;;")
    val word=w.split(";;;")
    val table = connection.getTable(TableName.valueOf("test:bangumi"))
    var list:Map[String,Int] = Map()
    for(i<- 0.to(word.length-1)) {
      val get: Get = new Get(Bytes.toBytes(word(i)))
      val result = table.get(get)
      var r: Array[Byte] = result.getValue(Bytes.toBytes("information"), Bytes.toBytes("information"))
      var sentence = Bytes.toString(r)
      if (sentence != null) {
        var lit = sentence.split(" #@! ")
        for (a <- 0.to(lit.length - 1)) {
        var map2: Map[String, Int] = Map(lit(a).split("~")(0) -> Integer.parseInt(lit(a).split("~")(1)))
        list = list ++ map2.map(t => t._1 -> (t._2 + list.getOrElse(t._1, 0)))
      }
      /*结果类似这种格式
        http://bangumi.tv/img/no_icon_subject.png --- http://bangumi.tv/subject/73206 --- 1+2=Paradise~2
        http://lain.bgm.tv/pic/cover/s/15/9f/19021_QzX5k.jpg --- http://bangumi.tv/subject/19021 --- +模型姐妹~1
        http://lain.bgm.tv/pic/cover/s/15/9f/19021_QzX5k.jpg --- http://bangumi.tv/subject/19021 --- +模型姐妹~1
      */
    }
    }
    return list.asJava
  }

  /*def scan(args: Array[String]): Array[String] = {

    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.set("hbase.zookeeper.quorum", "192.168.88.135,192.168.88.132,192.168.88.133")
    hbaseConf.set("hbase.zookeeper.property.clientPort", "2181")
    hbaseConf.set(TableInputFormat.INPUT_TABLE, "test:bangumi")

    def convertScanToString(scan: Scan) = {
      val proto = ProtobufUtil.toScan(scan)
      Base64.encodeBytes(proto.toByteArray)
    }
    val sparkConf = new SparkConf().setMaster("local").setAppName("demo")
    val sc = new SparkContext(sparkConf)

    var array:Array[String]= Array("1","1","1","1","1","1","1","1","1","1")
    var i=0
    val filterlist = new FilterList(FilterList.Operator.MUST_PASS_ONE)
    val scan = new Scan()

    for(arg<-args) {
      if(arg!=null&&arg.length>0) {
        val filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(arg))
        filterlist.addFilter(filter)
      }
  }
    scan.setFilter(filterlist)
    hbaseConf.set(TableInputFormat.SCAN, convertScanToString(scan))
    val hBaseRDD = sc.newAPIHadoopRDD(hbaseConf, classOf[TableInputFormat],
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable],
      classOf[org.apache.hadoop.hbase.client.Result])
    val count = hBaseRDD.count()
    for(rdd<-hBaseRDD){
      if(i<=9) {
        var result = rdd._2
        var r1: Array[Byte] = result.getValue(Bytes.toBytes("information"), Bytes.toBytes("information"))
        var line1 = Bytes.toString(r1)
        println("for:" + line1)
        array(i) = line1
        i+=1
      }
    }
    /*hBaseRDD.foreach{case (_, result) => {
      // 获取行键
      if(i<=9) {
        var r: Array[Byte] = result.getValue(Bytes.toBytes("information"), Bytes.toBytes("information"))
        var line = Bytes.toString(r)
        if(line!=null && line.length>0 && line!=1 && !line.equals("1")) {
          array(i) = line
          println("装入:"+array(i))
          i += 1
        }
      }
    }}*/
    sc.stop()

    for(a<-array){
      println("返回："+a)
    }
    return array
  }
*/
}
