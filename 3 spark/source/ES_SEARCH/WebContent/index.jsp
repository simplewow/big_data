<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<script type="text/javascript"></script>
</head>
<body>

<form action="search.do" method="post">
	<input type="hidden" value="1" name="num">
	<input type="hidden" value="0" name="count">
	<input name="keyword" size="30"> &nbsp; <input type="submit" value="千度一下">
</form>

<hr>
<c:if test="${! empty page.list }">
	千度为您找到相关结果约${page.totalCount}个<br>
	
	<c:forEach var="bean" items="${page.list }">
		<a href="${bean.url }" target="_blank">${bean.title } </a><br>
		${bean.content }<br>
	</c:forEach>

	<br>
	<c:forEach var="n" items="${page.numbers }">
		 <a href="javascript:">${n } </a>
	</c:forEach>
</c:if>

</body>
</html>