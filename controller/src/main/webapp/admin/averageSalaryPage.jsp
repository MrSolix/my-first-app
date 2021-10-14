<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Average Salary</title>
    <jsp:include page="../menu.jsp"/>
</head>
<body>
<h1>Average Salary Page</h1>

<form action="${pageContext.request.contextPath}/admin/averageSalary" method="post">
    <table>
        <tr>
            <td><b>Teacher UserName</b></td>
            <td><input type="text" name="userName"/></td>

        </tr>
        <tr>
            <td><b>Enter min range of months</b></td>
            <td><input type="number" name="minRange"></td>

        </tr>
        <tr>
            <td><b>Enter max range of months</b></td>
            <td><input type="number" name="maxRange"></td>
        </tr>
        <tr>
            <td colspan ="2">
                <input type="submit" value="Submit"/>
                <a href="${pageContext.request.contextPath}/homePage.jsp">Cancel</a>
            </td>
        </tr>
    </table>
    <c:if test="${averageSalary >= 0}">
            <b>Average Salary = </b>
            <b>${averageSalary}$</b>
    </c:if>
    <p style="color: red"><b>${errorStringInAvgSalaryPage}
        ${errorMonthsInAvgSalaryPage}
        ${errorNumberInAvgSalaryPage}</b></p>
</form>

</body>
</html>
