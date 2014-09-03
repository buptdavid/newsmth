/**
 * 
 */
package newsmth.crawl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.PriorityScheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @author weijielu
 *
 */
public class MainStart {
	
	public static Map<String, String> getBoardList() throws IOException{
		String folder = BoardProcessor.boardDirectory + "m.newsmth.net\\";
		Map<String, String> map = new HashMap<String, String>();
		BufferedReader reader = null;
		
		File fileFolder = new File(folder);
		if(fileFolder.isDirectory()){
			String[] files = fileFolder.list();
			for(int i = 0; i < files.length; i++){
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(folder + files[i]), "UTF-8"));
				String line = reader.readLine();
				JSONObject json = JSON.parseObject(line);
				map.put(json.getString("title"), json.getString("url"));
			}
		}
		
		
		return map;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		Map<String, String> map = getBoardList();
		Iterator it = map.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry entry = (Map.Entry) it.next();
			if(entry.getValue() != null){
				String url = entry.getValue().toString();
				System.out.println("Crawl " + entry.getKey() + " URL: " + url);
				 Spider.create(new PostProcessor(url.substring(url.lastIndexOf("/") + 1))).setScheduler(new PriorityScheduler()).addUrl(entry.getValue().toString())
//			        .addPipeline(new JsonFilePipeline("C:\\webmagic\\post\\"))
//			        .thread(5)
			                .run();
			}
			
		}
	}

}
