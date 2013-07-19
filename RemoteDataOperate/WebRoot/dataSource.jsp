<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>

<title>选择数据源</title>
<link rel="stylesheet" type="text/css" href="./src/common.css"/>
</head>
<body>
<div class="top"></div>
<div class="head">
	<div class="head_left"></div>
    <div class="head_middle"><font class="head_txt">&nbsp;选择数据源</font></div>
    <div class="head_right"></div>
</div>
<script language="javascript" type="text/javascript">
function validate()
{
	if(document.all.host.value==""
	||document.all.port.value==""
	||document.all.dbname.value==""
	||document.all.user.value==""
	||document.all.pswd.value==""
	||document.all.upload.value=="")
	{
		alert("请补充缺失的登录信息。");
		return false;
	}
	else
		return true;
}
</script>
<div class="main" >
<table height="20"></table>
<form id="form1" method="post" action="./LoadData" enctype="multipart/form-data" onsubmit='return validate();'>
	<table width="80%" align="center">
		<tr>
		<td class="tbleft">数据库服务器地址：</td><td class="tbright"><input type="text" name="host" onclick='select();' value="127.0.0.1"/></td>
		</tr>
		<tr>
		<td class="tbleft">数据库服务器端口号：</td><td class="tbright"><input type="text" name="port" onclick='select();' value="3306"/></td>
		</tr>
		<tr>
		<td class="tbleft">数据库名称：</td><td class="tbright"><input type="text" name="dbname" onclick='select();' value="data"/>
		<font color='red' size=2>*请保证数据库名称存在</font>
		</td>
		</tr>
		<tr>
		<td class="tbleft">用户名：</td><td class="tbright"><input type="text" name="user" onclick='select();' value="root"/></td>
		</tr>
		<tr>
		<td class="tbleft">密码：</td><td class="tbright"><input type="password" name="pswd" onclick='select();' value="admin"/></td>
		</tr>
		<tr>
		<td class="tbleft">数据源文件：</td><td class="tbright">
		<input type="file" name ="upload" style='width:210px;'/><font color='red' size=2>*数据文件较大时请耐心等待．．．</font>
		</td>
		</tr>
		
	</table>
	<div align="center">
		<button type='submit' class="button" ><font class="btn_txt" style=" margin-left:-10px;">写入数据</font></button>
	</div>
	</form>
	<br />
	<div class="bottom"></div>
</div>

</body>
</html>
