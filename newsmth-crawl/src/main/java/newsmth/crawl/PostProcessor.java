/**
 * 
 */
package newsmth.crawl;

import java.util.List;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Selectable;

/**
 * @author David
 *
 */
public class PostProcessor implements PageProcessor {
	
	private String board;
	
	public PostProcessor(){
		
	}
	
	public PostProcessor(String board){
		this.board = board;
		URL_LIST = "http://m\\.newsmth\\.net/board/" + board;
		URL_LIST_INDEX = "http://m\\.newsmth\\.net/board/" + board + "\\?p=\\d+";
		URL_POST = "http://m\\.newsmth\\.net/article/" + board + "/\\d+";
		URL_POST_INDEX = "http://m\\.newsmth\\.net/article/" + board + "/\\d+\\?p=\\d+";
	}
	
	public String URL_LIST = null;
//			"http://m\\.newsmth\\.net/board/" + board;
	
	public String URL_LIST_INDEX = null;
//			"http://m\\.newsmth\\.net/board/" + board + "\\?p=\\d+";

    public String URL_POST = null;
//    		"http://m\\.newsmth\\.net/article/" + board + "/\\d+";
    
    public String URL_POST_INDEX = null;
//    		"http://m\\.newsmth\\.net/article/" + board + "/\\d+\\?p=\\d+";
    
    private Site site = Site
            .me()
            .setRetryTimes(3)
            .setDomain("m.newsmth.net")
            .setSleepTime(1000)
            .setUserAgent(
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_2) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");

	/* (non-Javadoc)
	 * @see us.codecraft.webmagic.processor.PageProcessor#process(us.codecraft.webmagic.Page)
	 */
	public void process(Page page) {
		// 列表页
		if (page.getUrl().regex(URL_LIST).match() || page.getUrl().regex(URL_LIST_INDEX).match()) {
            page.addTargetRequests(page.getHtml().xpath("//div[@id=\"m_main\"]/ul[@class=\"list sec\"]").links().regex(URL_POST).all());
            
            String pageUrl = page.getUrl().toString();
            int currentPageIndex = 1;
            if(pageUrl.contains("=")){
            	currentPageIndex = Integer.valueOf(pageUrl.substring(pageUrl.indexOf("=") + 1));
            }
            
            List<String> tempURL_LIST = page.getHtml().xpath("//div[@id=\"m_main\"]//div[2]").links().regex(URL_LIST + "\\?p=" + (currentPageIndex + 1)).all();
            if(tempURL_LIST != null && tempURL_LIST.size() >= 1){
            	page.addTargetRequest(tempURL_LIST.get(0));
            }
            
            // 帖子页
        } else if(page.getUrl().regex(URL_POST).match() || page.getUrl().regex(URL_POST_INDEX).match()){
        	String pageUrl = page.getUrl().toString();
        	int currentPageIndex = 1;
            if(pageUrl.contains("=")){
            	currentPageIndex = Integer.valueOf(pageUrl.substring(pageUrl.indexOf("=") + 1));
            }
            
            List<String> tempURL_LIST_INDEX = page.getHtml().xpath("//div[@id=\"m_main\"]//div[2]").links().regex(URL_POST + "\\?p=" + (currentPageIndex + 1)).all();
            if(tempURL_LIST_INDEX != null && tempURL_LIST_INDEX.size() >= 1){
            	page.addTargetRequest(tempURL_LIST_INDEX.get(0));
            }
            
            grabContent(page);
        } 
	}
	
	private void grabContent(Page page){
		Selectable table = page.getHtml().xpath("//ul[@class='list sec']");
		
		page.putField("title", table.xpath("//li[1]/text()"));
		page.putField("url", page.getUrl().toString());
		for(int i = 2; i <= 11; i++){
			String content = table.xpath("//li[" + i + "]/div[@class='sp']/text()").toString();
			if(content != null && !content.equals("null")){
				page.putField("content" + (i - 2), content);
				page.putField("floor" + (i - 2), table.xpath("//li[" + i + "]//div[@class='nav hl']/div[1]/a[1]/text()"));
		        page.putField("author" + (i - 2), table.xpath("//li[" + i + "]//div[@class='nav hl']/div[1]/a[2]/text()"));
		        page.putField("date" + (i - 2), table.xpath("//li[" + i + "]//div[@class='nav hl']/div[1]/a[3]/text()"));
			}else{
				break;
			}
		}
	}

	public Site getSite() {
		return site;
	}
	
	public static void main(String[] args) {
        Spider.create(new PostProcessor("MobileApp")).setScheduler(new PriorityScheduler()).addUrl("http://m.newsmth.net/board/MobileApp")
//        .addPipeline(new JsonFilePipeline("C:\\webmagic\\post\\"))
//        .thread(5)
                .run();
    }

}
