<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Login</title>
    <jsp:include page="menu.jsp"/>
</head>
<body>

<h2>Login Page</h2>

<c:if test="${param.error != null}">
    <p style="color: red">Invalid username and password.</p>
</c:if>
<c:if test="${param.logout != null}">
    <p style="color: red">You have been logged out.</p>
</c:if>
<p style="color: red;">${errorMessage}${loginedError}</p>

<form name="f" action="${pageContext.request.contextPath}/login" method="post">

    <table>
        <tr>
            <td><b>Login</b></td>
            <td><input type="text" name="username" id="username"/></td>
        </tr>
        <tr>
            <td><b>Password</b></td>
            <td><input type="password" name="password" id="password"/></td>
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
