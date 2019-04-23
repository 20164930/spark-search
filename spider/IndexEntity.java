package neu;

import com.geccocrawler.gecco.annotation.*;
import com.geccocrawler.gecco.spider.HtmlBean;

public class IndexEntity implements HtmlBean {

    @Image({"data-original","src"})
    @HtmlField(cssPath = " a > span.image > img")
    private String imgUrl;

    @Text
    @HtmlField(cssPath = " div > h3 > a")
    private String title;

    @Attr("href")
    @HtmlField(cssPath = " div > h3 > a")
    private String id;


    @Text
    @HtmlField(cssPath = " div > p.info.tip")
    private String time;

    @Text
    @HtmlField(cssPath = " div > h3 > small")
    private String jpTitle="o_o ....";

    @Href(value="href")
    @HtmlField(cssPath = " div > h3 > a")
    private String url;



    public String getUrl() {
        return url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getJpTitle() {
        return jpTitle;
    }

    public String getId() {
        return id;
    }


    public void setUrl(String url) {
        this.url = url;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setJpTitle(String jpTitle) {
        if(jpTitle!=null) {
            this.jpTitle = jpTitle;
        }
    }


}
