package neu;


import com.geccocrawler.gecco.GeccoEngine;
import com.geccocrawler.gecco.annotation.Gecco;
import com.geccocrawler.gecco.annotation.HtmlField;
import com.geccocrawler.gecco.annotation.Request;
import com.geccocrawler.gecco.annotation.Text;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.spider.HtmlBean;

import java.util.List;

@Gecco(matchUrl = "http://bangumi.tv/anime/browser?page={page}",
        pipelines = "bilibilipipeline")
public class SpiderEntry implements HtmlBean {
    @Request
    private HttpRequest request;

    @Text
    @HtmlField(cssPath = "#columnSubjectBrowserA > div.section > div > div > strong")
    private String page;

    @HtmlField(cssPath = "#browserItemList > li")
    private List<IndexEntity> fanList;

    public List<IndexEntity> getFanList() {
        return fanList;
    }

    public String getPage() {
        return page;
    }

    public HttpRequest getRequest() {
        return request;
    }


    public void setFanList(List<IndexEntity> fanList) {
        this.fanList = fanList;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public static void main(String[] args) {

        // BasicConfigurator.configure();
        GeccoEngine.create()

                //Gecco搜索的包路径
                .classpath("neu")
                //开始抓取的页面地址
                .start("http://bangumi.tv/anime/browser?page=1")
                //开启几个爬虫线程
                .thread(1)
                //单个爬虫每次抓取完一个请求后的间隔时间
                .interval(500)
                .start();
    }


}
