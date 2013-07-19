package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import dbo.DB;

public class LoadData extends HttpServlet {

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setHeader("Pragma","No-cache");
		response.setHeader("Cache-Control","no-cache");
		response.setDateHeader("Expires", -1);
		response.setContentType("text/html");
		response.setContentType("text/html;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		
		
		

		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setSizeThreshold(4096);// 设置缓冲,这个值决定了是fileinputstream还是bytearrayinputstream
		//factory.setRepository(new File("d:\\temp"));//设置临时存放目录,默认是new File(System.getProperty("java.io.tmpdir"))
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setSizeMax(100*1024*1024);//100M
		//sfu.setHeaderEncoding("utf-8");  
        List fileList = null; 
        try {
            fileList = sfu.parseRequest(request);   
        } catch (FileUploadException e) {           
            //数据提取错误
        	warn(out,"表单数据解析错误！");
        	end(out);
        	return ;
        }
        Iterator it=fileList.iterator();
        String conStr,host="",port="",dbname="",user="",pswd="",rootPath="",filePath="";
        FileItem itemFile = null;
        while(it.hasNext())
        {
        	FileItem item=(FileItem)it.next();
        	
        	if(item.isFormField())
        	{
        		String name=item.getFieldName();//字段名称
       		 	String value=item.getString("utf-8");//字段值
        		 if("host".equals(name))//主机
        		 {
        			 host=value;
        		 }
        		 else if("port".equals(name))//端口
        		 {
        			 port=value;
        		 }
        		 else if("dbname".equals(name))//数据库名
        		 {
        			 dbname=value;
        		 }
        		 else if("user".equals(name))//用户
        		 {
        			 user=value;
        		 }
        		 else if("pswd".equals(name))//密码
        		 {
        			 pswd=value;
        		 }
        	}
        	else
        	{
        		String name=item.getName();
        		ServletContext application=this.getServletContext();
        		rootPath=application.getRealPath("/");
        		rootPath="D:/";
        		filePath="tmpData/"+name;
        		filePath=name;
        		itemFile=item;
        	}
        }
        conStr="jdbc:mysql://"+host+":"+port+"/"+dbname+"?user="+user+"&password="+pswd;//连接字符串
        HttpSession session=request.getSession();
        session.setAttribute("conStr", conStr);
  		DB db=new DB();
  		if(db.tryConnect(conStr))//连接成功
  		{
  			File file=new File(rootPath+filePath);//获得物理路径
    		try {
				itemFile.write(file);//写入数据
			} catch (Exception e) {
				//文件上传错误
				warn(out,"文件上传失败，请检查浏览器设置或者文件的合法性！");
	        	end(out);
	        	return ;
			}
  			//读取数据源文件，插入数据表
  			int rowsNum=db.insertData(rootPath,filePath,session);
  			String tbName=filePath.substring(filePath.lastIndexOf("/")+1);
  			if(tbName.contains("."))
  				tbName=tbName.substring(0,tbName.indexOf("."));
  			session.setAttribute("tbName", tbName);
  			if(rowsNum<=0)//插入错误
  			{
  				String msg="<script>alert('数据源存在非法数据信息，实际插入数据"+Math.abs(rowsNum)+"行。');";
  				if(rowsNum!=0)//插入有成功的
  				{
  					msg+="location.href='./dataView.jsp'";
  				}
  				else
  				{
  					msg+="location.href='./dataSource.jsp'";
  				}
  				msg+="</script>";
  				out.print(msg);
  			}
  			else
  			{
  				String msg="<script>alert('成功插入数据"+Math.abs(rowsNum)+"行。');location.href='./dataView.jsp'</script>";
  				out.print(msg);
  			}
  		}
  		else
  		{
  			//返回数据连接错误信息
    		warn(out,"数据库链接错误!请检查数据库连接信息的合法性。");
    		end(out);
    		return ;
  		}
  		end(out);
	}

	/**
	 * 提示错误
	 * @param out 输出
	 * @param msg 信息
	 */
	private void warn(PrintWriter out,String msg)
	{
		out.println("<script language=javascript>" +
				"alert('"+msg+"');" +
				"location.href='./dataSource.jsp';"+
			"</script>");
	}
	private void end(PrintWriter out)
	{
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}
}
