package dbo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

public class DB {
	
	Connection con;
	Statement stmt;
	ResultSet rslt;
	/**
	 * 尝试连接数据库
	 * @param conStr 连接字符串
	 * @return 连接成功否
	 */
	public boolean tryConnect(String conStr)
	{
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(conStr);
			stmt=con.createStatement();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	/**
	 * 执行SQL语句
	 * @param cmd SQL语句
	 * @return 执行成功否
	 * @throws SQLException 
	 */
	public void execute(String cmd) throws SQLException
	{
		if(cmd.contains("select"))
		{
			rslt=stmt.executeQuery(cmd);
		}
		else
		{
			stmt.executeUpdate(cmd);
		}	
	}
	/**
	 * 获取查询结果集
	 * @return 结果集
	 */
	public ResultSet getResult()
	{
		return rslt;
	}
	@SuppressWarnings("finally")
	public int insertData(String rootPath,String filePath,HttpSession session)
	{
		String fileName=filePath.substring(filePath.lastIndexOf("/")+1);
		if(fileName.contains("."))
			fileName=fileName.substring(0,fileName.indexOf("."));
		File file=new File(rootPath+filePath);
		FileReader fr = null;
		BufferedReader br = null;
		int lineNum=0;//受影响的行数
		try {
			fr=new FileReader(file);
			br = new BufferedReader(fr);
			String headLine="",firstLine="",line="";
			headLine=br.readLine();//标题
			firstLine=br.readLine();//数据类型
			StringTokenizer tokenHead=new StringTokenizer(headLine,",");
			StringTokenizer tokenFirst=new StringTokenizer(firstLine,",");
			String sqlCreateTable="create table "+fileName+"(";
			String insertSQL="insert into "+fileName+" values(";
			ArrayList<String> nameList=new ArrayList<String>();
			while(tokenHead.hasMoreTokens()&&tokenFirst.hasMoreTokens())
			{
				String fieldName=tokenHead.nextToken();
				nameList.add(fieldName);
				sqlCreateTable+=fieldName+" ";//字段名
				Pattern patternNum = Pattern.compile("^-?\\d+$");
				Pattern patternFloat = Pattern.compile("^(-?\\d+)(\\.\\d+)?$");
				String type=tokenFirst.nextToken();
				insertSQL+=type+",";
				Matcher isNum = patternNum.matcher(type);
				Matcher isFloat=patternFloat.matcher(type);
				if (isNum.matches()) {
					sqlCreateTable+="int,";
				}
				else if(isFloat.matches())
				{
					sqlCreateTable+="float,";
				}
				else
				{
					sqlCreateTable+="varchar(100),";
				}
			}
			session.setAttribute("nameList", nameList);
			sqlCreateTable=sqlCreateTable.substring(0,sqlCreateTable.lastIndexOf(","));
			insertSQL=insertSQL.substring(0,insertSQL.lastIndexOf(","));
			sqlCreateTable+=");";
			insertSQL+=");";
			this.execute("drop table if exists "+fileName+";");
			this.execute(sqlCreateTable);
			this.execute(insertSQL);
			lineNum++;
			while(line!=null)
			{
				line=br.readLine();
				if(line!=null)
				{
					String insert="insert into "+fileName+" values(";
					StringTokenizer token=new StringTokenizer(line,",");
					while(token.hasMoreTokens())
					{
						insert+=token.nextToken()+",";
					}
					insert=insert.substring(0,insert.lastIndexOf(","));
					insert+=");";
					this.execute(insert);
					lineNum++;
				}
			}
			
			
		} 
		catch (Exception e)
		{
			lineNum=-lineNum;
		}
		finally
		{
			try {
				con.close();
				br.close();
				fr.close();
			} catch (final Exception e) {
			}
			file.delete();
			return lineNum;
		}
	}
}
