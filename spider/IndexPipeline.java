package neu;


import com.geccocrawler.gecco.annotation.PipelineName;
import com.geccocrawler.gecco.pipeline.Pipeline;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.scheduler.SchedulerContext;

import java.util.List;

@PipelineName(value="bilibilipipeline")
public class IndexPipeline implements Pipeline<SpiderEntry> {

    public void process(SpiderEntry spiderEntry) {
        HttpRequest request=spiderEntry.getRequest();
        List<IndexEntity> fanList=spiderEntry.getFanList();
        for(IndexEntity i:fanList){
            /*
            0:id
            1:title
            2:jpTitle
            3:time+directior
            4:imageUrl
            5:url
            */
            String line=i.getId()+"!@#"+i.getTitle()+"!@#"+i.getJpTitle()+
                    "!@#"+i.getTime()+"!@#"+i.getImgUrl()+"!@#"+i.getUrl();
            KafkaRead.run(line);
        }

        int page = Integer.parseInt(spiderEntry.getPage()) + 1;
        String nextPageurl = "http://bangumi.tv/anime/browser?page=" + page;
        System.out.println("num_of_pages:"+" "+(page-1));
        SchedulerContext.into(request.subRequest(nextPageurl));

    }
}
