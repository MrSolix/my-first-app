<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="student" value="STUDENT"/>
<c:set var="teacher" value="TEACHER"/>
<c:set var="admin" value="ADMIN"/>
<c:choose>
    <c:when test="${loginedUser.role.getType().equals(admin)}">
<a href="${pageContext.request.contextPath}/admin/admin">
    Admin Page
</a>
||
    </c:when>
    <c:when test="${loginedUser.role.getType().equals(teacher)}">
<a href="${pageContext.request.contextPath}/teacher/teacher">
    Teacher Page
</a>
||
    </c:when>
    <c:when test="${loginedUser.role.getType().equals(student)}">
<a href="${pageContext.request.contextPath}/student/student">
    Student Page
</a>
||
    </c:when>
</c:choose>
<a href="${pageContext.request.contextPath}/main/homePage.jsp">
    Home Page
</a>
||
<a type="" href="${pageContext.request.contextPath}/main/userInfoPage.jsp">
    User Info
</a>
||
<c:if test="${loginedUser == null}">
<a href="${pageContext.request.contextPath}/registration">
    Registration
</a>
||
</c:if>
<a href="${pageContext.request.contextPath}/main/loginPage.jsp">
    Login
</a>
||
<a href="${pageContext.request.contextPath}/main/logout">
    Logout
</a>

<span style="color:red">[ ${loginedUser.userName} ]</span>