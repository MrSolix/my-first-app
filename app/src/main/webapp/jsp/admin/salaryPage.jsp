<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Salary</title>
    <jsp:include page="../menu.jsp"/>
</head>
<body>
<h1>Salary Page</h1>

<form action="${pageContext.request.contextPath}/admin/salary" method="post">
    <table>
        <tr>
            <td><b>Teacher Name</b></td>
            <td><input type="text" name="userName"/></td>
            <c:if test="${teacher != null}">
                <td><b>Salary = </b></td>
                <td><b>${teacher.salary}$</b></td>
            </c:if>
                <td style="color: red"><b>${errorStringInSalaryPage}</b></td>
        </tr>
        <tr>
            <td colspan ="2">
                <input type="submit" value="Submit"/>
                <a href="${pageContext.request.contextPath}/main/home">Cancel</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
