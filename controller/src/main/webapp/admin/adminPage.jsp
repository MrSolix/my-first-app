<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Admin</title>
    <jsp:include page="../menu.jsp"/>
</head>
<body>

<h1>Admin Page</h1>

<a href="${pageContext.request.contextPath}/admin/salary">
    <h3>Salary Page</h3>
</a>
<a href="${pageContext.request.contextPath}/admin/average-salary">
    <h3>Average Salary Page</h3>
</a>

</body>
</html>
