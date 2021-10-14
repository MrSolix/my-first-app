<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>User Info</title>
    <jsp:include page="menu.jsp"/>
</head>
<body>

<h2>User Info Page</h2>

<b>${loginedUser.getInfo()}</b>

</body>
</html>
