
import neu.Findword;
import neu.MapValueComparator;
import neu.Relative;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;

import java.io.IOException;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "GetServlet",
urlPatterns = {"/get.html"})
public class searchServerlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String keyword = request.getParameter("text");
		Map<String, Object> list = Findword.run(keyword);
		if (list != null && list.size() > 0) {
			Map<String, Integer> lis = new HashMap<String, Integer>();
			Iterator<Map.Entry<String, Object>> map1 = list.entrySet().iterator();
			while (map1.hasNext()) {
				Map.Entry<String, Object> entry = map1.next();
				lis.put(entry.getKey(), (Integer) entry.getValue());
			}

			Iterator<Map.Entry<String, Integer>> map = lis.entrySet().iterator();
			String[] director=new String[lis.size()];
			int i=0;
			while (map.hasNext()) {
				Map.Entry<String, Integer> entry = map.next();
				if(entry.getKey().split(" --- ").length>=5) {
					String[] temp = entry.getKey().split(" --- ")[4].split(" / "); //取出导演
					if (temp.length >= 1) {
						StopRecognition filter = new StopRecognition();
						filter.insertStopNatures("w");
						String w= DicAnalysis.parse(temp[temp.length-1]).recognition(filter).toStringWithOutNature(";;;");
						String[] words=w.split(";;;");//对导演进行分词
						for(String word:words) {
							if(i<lis.size()&&word!=null&&word!="") {
								director[i] = word; //分词后的关键字进行关联分析
								i++;
							}
						}
					}
				}
			}
			Relative re=new Relative();
			String[] relative = re.scan(director);//关联分析
			for(String a:relative){
				System.out.println(a);
			}
			request.setAttribute("relative",relative);
			request.setAttribute("list", lis);
			request.getRequestDispatcher("showurl.jsp").forward(request, response);
		}
	}

}
