<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<c:set var="student" value="STUDENT"/>
<c:set var="teacher" value="TEACHER"/>
<c:set var="admin" value="ADMIN"/>
<sec:authorize access="hasRole('ADMIN')">
    <a href="${pageContext.request.contextPath}/admin">
        Admin Page
    </a>
    ||
</sec:authorize>
<sec:authorize access="hasRole('TEACHER')">
    <a href="${pageContext.request.contextPath}/teacher">
        Teacher Page
    </a>
    ||
</sec:authorize>
<sec:authorize access="hasRole('STUDENT')">
    <a href="${pageContext.request.contextPath}/student">
        Student Page
    </a>
    ||
</sec:authorize>
<a href="${pageContext.request.contextPath}/main/home">
    Home Page
</a>
||
<a type="" href="${pageContext.request.contextPath}/main/user-info">
    User Info
</a>
||
<sec:authorize access="!isAuthenticated()">
    <a href="${pageContext.request.contextPath}/registration">
        Registration
    </a>
    ||
    <a href="${pageContext.request.contextPath}/login">
        Login
    </a>
</sec:authorize>
<sec:authorize access="isAuthenticated()">
    <a href="${pageContext.request.contextPath}/logout">
        Logout
    </a>
    <span style="color:red">[ <sec:authentication property="name"/> ]</span>
</sec:authorize>
