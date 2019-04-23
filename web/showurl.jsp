<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ page import="neu.*" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Iterator" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>

<link rel="stylesheet" href="show.css" type="text/css" />
	<style>
		a{ text-decoration:none}
	</style>
</head>
<body>

<div class="search bar7">
        <form action="<%=request.getContextPath()%>/get.html" method="post">
            <input type="text" placeholder="请输入关键字" name="text">
            <button type="submit"></button>
        </form>
</div>


<div class="show">
	<%
		Map<String,Integer> l=(Map<String,Integer>)request.getAttribute("list");

		if(l!=null && l.size()>0) {
			Sort sort = new Sort();
			Map<String, Integer> list = sort.sortMapByValue(l);
			Iterator<Map.Entry<String, Integer>> map = list.entrySet().iterator();
			while (map.hasNext()) {
				Map.Entry<String, Integer> entry = map.next();
				if(entry.getKey().split(" --- ").length>=5){
				String imgurl = entry.getKey().split(" --- ")[0];
				String url = entry.getKey().split(" --- ")[1];
				String title = entry.getKey().split(" --- ")[2];
				String jptitle=entry.getKey().split(" --- ")[3];
				String time_direction=entry.getKey().split(" --- ")[4];
				int match = entry.getValue();
				if(match>=1) { //最小匹配标准
					out.print("<img src=\"" + imgurl + "\" width=\"100px\" height=\"120px\"/><br>");
					out.print("<a href=\"" + url + "\" style=color:#000000;\">" + title + "</a><br>");
					out.print("<span style=\"font-size:10px;color:#d0d0d0;\">" + jptitle + "</span><br>");
					out.print("<span style=\"font-size:10px;color:#d0d0d0;\">" + time_direction + "</span><br>");
					out.print("<span style=\"font-size:10px;color:#d0d0d0;\">" + url + "</span><br><br><br>");
				}
				}
			}
		}else{
			System.out.println("无搜索结果");
			out.print("<br><br>无搜索结果<br><br>");
		}


		String[] relative=(String[])request.getAttribute("relative");
		for(int i=0;i<relative.length;i++){
			if(relative[i]!=null && relative[i].length()>0){
				String[] s=relative[i].split(" #@! ");
				for(String ss:s){
					String imgurl = ss.split(" --- ")[0];
					String url = ss.split(" --- ")[1];
					String title = ss.split(" --- ")[2];
					String jptitle=ss.split(" --- ")[3];
					String time_direction=ss.split(" --- ")[4];
					out.print("<h2>猜你喜欢</h2>");
					out.print("<img src=\"" + imgurl + "\" width=\"100px\" height=\"120px\"/><br>");
					out.print("<a href=\""+url+"\" style=color:#000000;\">"+title + "</a><br>");
					out.print("<span style=\"font-size:10px;color:#d0d0d0;\">"+jptitle+ "</span><br>");
					out.print("<span style=\"font-size:10px;color:#d0d0d0;\">"+time_direction+ "</span><br>");
					out.print("<span style=\"font-size:10px;color:#d0d0d0;\">"+url + "</span><br><br><br>");
				}
			}
		}


	%>

</div>	
</body>
</html>