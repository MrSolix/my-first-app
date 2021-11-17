<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Registration</title>
    <jsp:include page="menu.jsp"/>
</head>
<body>

<h2>Registration Page</h2>

<p style="color: red;">${errorMessage}${loginedError}${successMessage}</p>

<form action="${pageContext.request.contextPath}/registration" method="post">
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
            <td><b>Name</b></td>
            <td><input type="text" name="name"/></td>
        </tr>
        <tr>
            <td><b>Age</b></td>
            <td><input type="number" name="age"/></td>
        </tr>
        <tr>
            <td><b>What is your status?</b></td>
        </tr>
        <tr>
            <td><input type="radio" name="status" value="student" checked><b>Student</b></td>
            <td><input type="radio" name="status" value="teacher"><b>Teacher</b></td>
        </tr>
        <tr>
            <td>
                <input type="submit" value="Submit"/>
                <a href="${pageContext.request.contextPath}/homePage.jsp">Cancel</a>
            </td>
        </tr>
    </table>
</form>
</body>
</html>
