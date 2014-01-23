<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>index page</title>
</head>
<body>
<h1>
    <c:choose>
        <c:when test="${user ne null && (user.username ne null || user.username ne '')}">
            Hello ${user.username} !
        </c:when>
        <c:otherwise>Hello World !</c:otherwise>
    </c:choose>
</h1>

<%--<p>--%>
<%--<b>用户入口</b>--%>
<%--<form name="loginForm" action="${pageContext.request.contextPath}/user/login.do">--%>
    <%--用户名:<input type="text" name="username">--%>
    <%--密码:<input type="password" name="password">--%>
    <%--跳转类型:<select name="type">--%>
        <%--<option value="">不跳转</option>--%>
        <%--<option value="toProductCode">跳至您的进出口</option>--%>
    <%--</select>--%>
    <%--<input type="submit" value="登录跳转">--%>
<%--</form>--%>
<%--</p>--%>

</body>
</html>