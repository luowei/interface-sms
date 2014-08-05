<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>意见反馈</title>


    <style>
        * {
            margin: 0;
            padding: 0;
        }

        img {
            height: auto;
            max-width: 100%;
            border: 0;
        }

        body {
            font-size: 4vw;
            width: auto;
            font-family: Microsoft YaHei, MingLiU;
        }

        .main {
            margin: 1em;
            padding: 2em;
            width: auto;
        }

        h1 {
            font-size: 150%;
            margin: 1.5em 0;
            padding: 1em;
            text-align: center;
            box-shadow: 0 -1px 10px rgba(0, 0, 0, 0.1);
            background-color: #FAFAFA;
            background-image: -webkit-linear-gradient(top, #FFF, #F2F2F2);
            background-image: -o-linear-gradient(top, #FFF, #F2F2F2);
            background-image: -ms-linear-gradient(top, #FFF, #F2F2F2);
            background-image: linear-gradient(top, #FFF, #F2F2F2);
            background-repeat: repeat-x;
        }

        h1 a:link, h1 a:visited {
            color: #06c;
        }

        h1 a:hover {
            color: #09f;
        }

        strong {
            font-size: 150%;
        }

        h2 {
            font-size: 100%;
            margin-top: 1.5em;
        }

        p {
            font-size: 80%;
            padding: 0.6em 0;
            border-bottom: 1px solid #ddd;
        }
    </style>

</head>

<body>

<div class="main">


    <h1>意见反馈</h1>
    <c:if test="${msg eq 'ok'}">
        <h2>谢谢你的参与！</h2>
    </c:if>
    <c:if test="${msg ne 'ok'}">
        <form action="${ctx}/html/addSuggestion.do?accessToken=${accessToken}" method="post">
            <p>手机号码不会被公开。 必填项已用*标注</p>
            <br>

            <div><label for="author">姓名 *</label><br><input id="author" name="author" value="${user.realName}" size="30"
                                                            aria-required="true" type="text"></div>
            <div><label for="username">手机 *</label><br><input id="username" name="username" value="${user.username}" size="30"
                                                              aria-required="true" type="text"></div>
            <div><label for="comment">意见反馈 *</label><br><textarea id="comment" name="comment" cols="30" rows="8"
                                                                  aria-required="true"></textarea></div>
            <div><input value="提交" type="submit"></div>
        </form>
    </c:if>

</div>


</body>
</html>
