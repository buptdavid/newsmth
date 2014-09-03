/**
 * 
 */
package newsmth.crawl;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

/**
 * @author weijielu
 *
 */
public class BoardProcessor implements PageProcessor{
	public static final String boardDirectory = "I:\\webmagic\\board\\";
	
	public static final String SECTION_START = "http://m\\.newsmth\\.net/section";
	
	public static final String SECTION_LIST = "http://m\\.newsmth\\.net/section/\\d+";
	
	public static final String SECTION_URL = "http://m\\.newsmth\\.net/section/\\w+";
	
	public static final String BOARD_URL = "http://m\\.newsmth\\.net/board/\\w+";

	 private Site site = Site
	            .me()
	            .setDomain("m.newsmth.net")
	            .setSleepTime(3000)
	            .setUserAgent(
	                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
	
	public void process(Page page) {
		if(page.getUrl().regex(SECTION_START).match() || page.getUrl().regex(SECTION_LIST).match() || page.getUrl().regex(SECTION_URL).match()){
			page.addTargetRequests(page.getHtml().xpath("//div[@id=\"m_main\"]/ul[@class=\"slist sec\"]").links().regex(SECTION_LIST).all());
			page.addTargetRequests(page.getHtml().xpath("//div[@id=\"m_main\"]/ul[@class=\"slist sec\"]").links().regex(SECTION_URL).all());
			page.addTargetRequests(page.getHtml().xpath("//div[@id=\"m_main\"]/ul[@class=\"slist sec\"]").links().regex(BOARD_URL).all());
		} else if(page.getUrl().regex(BOARD_URL).match()){
			page.putField("url", page.getUrl().toString());
			page.putField("title", page.getHtml().xpath("//div[@id='wraper']/div[@class='menu sp']/text()"));
		} 
	}

	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
        Spider.create(new BoardProcessor()).setScheduler(new PriorityScheduler()).addUrl("http://m.newsmth.net/section")
        .addPipeline(new JsonFilePipeline(boardDirectory))
        .thread(5)
                .run();
    }

}
