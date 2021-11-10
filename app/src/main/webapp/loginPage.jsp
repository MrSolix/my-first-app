<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Login</title>
    <jsp:include page="menu.jsp"/>
</head>
<body>

<h2>Login Page</h2>

<p style="color: red;">${errorMessage}${loginedError}</p>

<form action="${pageContext.request.contextPath}/login" method="post">
    <table>
        <tr>
            <td><b>Login</b></td>
            <td><input type="text" name="userName"/></td>
        </tr>
        <tr>
            <td><b>Password</b></td>
            <td><input type="password" name="password"/></td>
        </tr>
        <tr>
            <td colspan ="2">
                <input type="submit" value="Submit"/>
                <a href="${pageContext.request.contextPath}/main/homePage.jspe.jsp">Cancel</a>
            </td>
        </tr>
    </table>
</form>

</body>
</html>
