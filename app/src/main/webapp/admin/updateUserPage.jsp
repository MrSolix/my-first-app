<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Update User</title>
    <jsp:include page="/menu.jsp"/>
    <script>
        function myFunction(id1, id2) {
            var checkBox = id1;
            var text = id2;
            if (checkBox.checked == true) {
                text.style.display = "block";
            } else {
                text.style.display = "none";
            }
        }
    </script>
</head>
<body>

<p style="color: red;">${errorMessage}</p>
<h3 style="color: darkgreen">Enter the username you want to change.</h3>
<form action="${pageContext.request.contextPath}/admin/update-user" method="post">
    <table>

        <tr>
            <td><b>Login</b></td>
            <td><input type="text" name="userLogin"/></td>

        </tr>
        <tr><td></td></tr>
        <tr>
            <td><b style="color: darkgreen">What is need changed?</b></td>
        </tr>
        <tr>
            <td>
                <b>Login</b>
                <input type="checkbox" id="login1" name="check" value="login" onclick="myFunction(
                        document.getElementById(id),
                        document.getElementById('login2'))">
            </td>
            <td id="login2" style="display:none"><input type="text" name="userName"/></td>
        </tr>
        <tr>
            <td>
                <b>Password</b>
                <input type="checkbox" id="pass1" name="check" value="pass" onclick="myFunction(
                        document.getElementById(id),
                        document.getElementById('pass2'))">
            </td>
            <td id="pass2" style="display:none"><input type="password" name="password"/></td>
        </tr>
        <tr>
            <td>
                <b>Name</b>
                <input type="checkbox" id="name1" name="check" value="name" onclick="myFunction(
                        document.getElementById(id),
                        document.getElementById('name2'))">
            </td>
            <td id="name2" style="display:none"><input type="text" name="name"/></td>
        </tr>
        <tr>
            <td>
                <b>Age</b>
                <input type="checkbox" id="age1" name="check" value="age" onclick="myFunction(
                        document.getElementById(id),
                        document.getElementById('age2'))">
            </td>
            <td id="age2" style="display:none"><input type="number" name="age"/></td>
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
