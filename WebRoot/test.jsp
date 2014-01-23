<%--
  Created by IntelliJ IDEA.
  User: luowei
  Date: 13-10-10
  Time: 下午5:45
  To change this template use File | Settings | File Templates.
--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<html>--%>
<%--<head>--%>
    <%--<title></title>--%>
<%--</head>--%>
<%--<body>--%>

<%--</body>--%>
<%--</html>--%>

<%@page buffer="5kb" autoFlush="true" %>

<%
    for(int i = 0; i < 10; i++){
        out.println("<html><body><img src=http://www.baidu.com/img/bdlogo.gif /></body></html>");
    }
%>