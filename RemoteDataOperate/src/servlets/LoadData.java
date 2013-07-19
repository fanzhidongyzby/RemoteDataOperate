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
		factory.setSizeThreshold(4096);// ���û���,���ֵ��������fileinputstream����bytearrayinputstream
		//factory.setRepository(new File("d:\\temp"));//������ʱ���Ŀ¼,Ĭ����new File(System.getProperty("java.io.tmpdir"))
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setSizeMax(100*1024*1024);//100M
		//sfu.setHeaderEncoding("utf-8");  
        List fileList = null; 
        try {
            fileList = sfu.parseRequest(request);   
        } catch (FileUploadException e) {           
            //������ȡ����
        	warn(out,"�����ݽ�������");
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
        		String name=item.getFieldName();//�ֶ�����
       		 	String value=item.getString("utf-8");//�ֶ�ֵ
        		 if("host".equals(name))//����
        		 {
        			 host=value;
        		 }
        		 else if("port".equals(name))//�˿�
        		 {
        			 port=value;
        		 }
        		 else if("dbname".equals(name))//���ݿ���
        		 {
        			 dbname=value;
        		 }
        		 else if("user".equals(name))//�û�
        		 {
        			 user=value;
        		 }
        		 else if("pswd".equals(name))//����
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
        conStr="jdbc:mysql://"+host+":"+port+"/"+dbname+"?user="+user+"&password="+pswd;//�����ַ���
        HttpSession session=request.getSession();
        session.setAttribute("conStr", conStr);
  		DB db=new DB();
  		if(db.tryConnect(conStr))//���ӳɹ�
  		{
  			File file=new File(rootPath+filePath);//�������·��
    		try {
				itemFile.write(file);//д������
			} catch (Exception e) {
				//�ļ��ϴ�����
				warn(out,"�ļ��ϴ�ʧ�ܣ�������������û����ļ��ĺϷ��ԣ�");
	        	end(out);
	        	return ;
			}
  			//��ȡ����Դ�ļ����������ݱ�
  			int rowsNum=db.insertData(rootPath,filePath,session);
  			String tbName=filePath.substring(filePath.lastIndexOf("/")+1);
  			if(tbName.contains("."))
  				tbName=tbName.substring(0,tbName.indexOf("."));
  			session.setAttribute("tbName", tbName);
  			if(rowsNum<=0)//�������
  			{
  				String msg="<script>alert('����Դ���ڷǷ�������Ϣ��ʵ�ʲ�������"+Math.abs(rowsNum)+"�С�');";
  				if(rowsNum!=0)//�����гɹ���
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
  				String msg="<script>alert('�ɹ���������"+Math.abs(rowsNum)+"�С�');location.href='./dataView.jsp'</script>";
  				out.print(msg);
  			}
  		}
  		else
  		{
  			//�����������Ӵ�����Ϣ
    		warn(out,"���ݿ����Ӵ���!�������ݿ�������Ϣ�ĺϷ��ԡ�");
    		end(out);
    		return ;
  		}
  		end(out);
	}

	/**
	 * ��ʾ����
	 * @param out ���
	 * @param msg ��Ϣ
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
