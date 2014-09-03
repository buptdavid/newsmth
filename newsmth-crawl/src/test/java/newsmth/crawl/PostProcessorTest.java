package newsmth.crawl;

import org.junit.Test;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

public class PostProcessorTest {
	
	@Test
	public void testBoard(){
		  Spider
		  .create(new PostProcessor("FamilyLife"))
		  .setScheduler(new PriorityScheduler())
		  .addUrl("http://m.newsmth.net/board/FamilyLife")
        .addPipeline(new JsonFilePipeline("I:\\webmagic\\post\\FamilyLife\\"))
        .thread(5)
                .run();
	}

}
