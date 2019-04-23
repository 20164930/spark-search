package neu;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

public class Relative {

    public  String[] scan(String[] args) {
        String[] relative=null;
        System.setProperty("hadoop.home.dir", "F:\\hadoop-2.7.6");
        Configuration config = HBaseConfiguration.create();
        config.set("hbase.zookeeper.quorum", "192.168.88.135,192.168.88.132,192.168.88.133");
        config.set("hbase.zookeeper.property.clientPort", "2181");
        config.set("hbase.master", "192.168.88.128:16000");
        try {
            Connection connection = ConnectionFactory.createConnection(config);
            Table table = connection.getTable(TableName.valueOf("test:bangumi"));
            relative=scantable(table,args);
            table.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return relative;
    }


    private  String[] scantable(Table table,String[] args) throws IOException {
        int i=0;
        String[] list=new String[10];
        Scan scan = new Scan();
        FilterList filterlist = new FilterList(FilterList.Operator.MUST_PASS_ONE);

        for(String arg:args) {
            if(arg!=null&&arg.length()>0) {
                Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new SubstringComparator(arg));
                filterlist.addFilter(filter);
            }
        }
        scan.setFilter(filterlist);

        ResultScanner rs = table.getScanner(scan);
        for (Result result : rs) {
            String rowkey=Bytes.toString(result.getRow());
            Get get = new Get(Bytes.toBytes(rowkey));
            Result rrr = table.get(get);
            String r=Bytes.toString(rrr.getValue(Bytes.toBytes("information"), Bytes.toBytes("information")));
            if(r!=null&&i<10) {
                list[i]=r;
                i++;
            }
        }
        return list;
    }

}
