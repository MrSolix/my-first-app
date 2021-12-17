<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Delete</title>
    <jsp:include page="/menu.jsp"/>
</head>
<body>
<h1>Delete Page</h1>

<form action="${pageContext.request.contextPath}/admin/delete-user" method="post">
    <table>
        <tr>
            <td><b>User Name</b></td>
            <td><input type="text" name="userName"/></td>
            <c:if test="${user != null}">
                <td style="color: lawngreen"><b>${successMessage}</b></td>
            </c:if>
            <td style="color: red"><b>${errorMessage}</b></td>
        </tr>
        <tr>
            <td colspan ="2">
                <input type="submit" value="Submit"/>
                <a href="${pageContext.request.contextPath}/main/homePage.jsp">Cancel</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
