<%@ page language="java" import="java.util.*" import="java.util.ArrayList"
import ="java.sql.ResultSet" 
import ="dbo.DB" 
pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>数据查询</title>
<link rel="stylesheet" type="text/css" href="./src/common.css"/>
</head>
<body onload="document.all.id1.focus();">
<div class="top"></div>
<div class="head">
	<div class="head_left"></div>
    <div class="head_middle"><font class="head_txt">&nbsp;数据查询</font></div>
    <div class="head_right"></div>
</div>
<div class="main" >

<table height="20"></table>
	<table width="80%" align="center" class="txt">
	<tr align="left">
		<td colspan="3">过滤条件：id1=<input id="id1" class="txt" type="text" value=""  onkeydown="if(event.keyCode==13) document.all.getData.click();" onclick="select();"/>
		<input type="button" id="getData" value="查询" class="txt" onclick="var cond=document.all.id1.value;location.href='./dataView.jsp?id1='+cond;"/>
		<input type="button" id="getAllData" value="显示所有数据" class="txt"
		onclick="location.href='./dataView.jsp';"/>
		</td>
		</tr>
	</table>
	<%
	
		DB db=new DB();
		if(session.getAttribute("conStr")!=null)
		{
			String tbName=session.getAttribute("tbName").toString();
			db.tryConnect(session.getAttribute("conStr").toString());
			if(request.getParameter("id1")!=null)//条件查询
			{
				try {
					db.execute("select * from "+tbName+" where id1="+request.getParameter("id1").toString()+";");
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
			else
			{
				//全部查询
				try {
					db.execute("select * from "+tbName+";");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		else
		{
			//禁止访问
			response.sendRedirect("./dataSource.jsp");
		}
	%>
	<table width="80%" align="center" border="1"  style="BORDER-COLLAPSE:collapse" class="txt">
		<tr align="center" bgcolor="#CCCCCC">
		<%
		int nums=0;//记录查询结果行数
			//获取查询数据
			if(session.getAttribute("nameList")==null)//非法访问
			{
				response.sendRedirect("./dataSource.jsp");
			}
			else
			{
				ArrayList<String> nameList=(ArrayList<String>)session.getAttribute("nameList");
				for(int i=0;i<nameList.size();i++)
				{
		%>
				<td><%=nameList.get(i) %></td>
		<%
				}
				ResultSet rslt=db.getResult();
				
			if(rslt!=null){
				while(rslt.next())
				{
					nums++;
		%>
					<tr align="center">
		<%
					for(int i=0;i<nameList.size();i++)
					{
		%>
						<td><%=rslt.getString(i+1)%></td>
		<%						
					}
		%>
					</tr>
		<%					
				}
				}
			}
		%>
		
	</table>
	<table width="80%" align="center" class="txt">
	<tr align="left">
		<td>
		查询结果行数：<%=nums%>行
		</td>
		</tr>
	</table>
	<div align="right" style="margin-right:20px">
		<button type="button" class="back_btn" onclick="window.location.href='./dataSource.jsp'"><font class="btn_txt" style=" margin-left:10px;">继续</font></button>
	</div>
	<br />
	<div class="bottom"></div>
</div>
</body>
</html>
