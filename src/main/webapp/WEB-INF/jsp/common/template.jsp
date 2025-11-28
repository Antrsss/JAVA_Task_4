<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Bookstore - ${pageTitle}</title>
  <link href="${pageContext.request.contextPath}/webjars/bootstrap/5.1.3/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <a class="navbar-brand" href="${pageContext.request.contextPath}/">Bookstore</a>
      <div class="navbar-nav me-auto">
        <c:if test="${not empty sessionScope.user}">
          <a class="nav-link" href="${pageContext.request.contextPath}/users">Users</a>
          <a class="nav-link" href="${pageContext.request.contextPath}/orders">Orders</a>
          <a class="nav-link" href="${pageContext.request.contextPath}/supplies">Supplies</a>
        </c:if>
      </div>
      <div class="navbar-nav">
        <c:choose>
          <c:when test="${not empty sessionScope.user}">
            <span class="navbar-text me-3">
              Hello, ${sessionScope.user.name}
              (${sessionScope.user.getClass().simpleName})
            </span>
            <a class="nav-link" href="${pageContext.request.contextPath}/auth/logout">Logout</a>
          </c:when>
          <c:otherwise>
            <a class="nav-link" href="${pageContext.request.contextPath}/auth/login">Login</a>
            <a class="nav-link" href="${pageContext.request.contextPath}/auth/register">Register</a>
          </c:otherwise>
        </c:choose>
      </div>
    </div>
  </nav>
  <div class="container mt-4">
    <jsp:include page="${contentPage}" />
  </div>
  <script src="${pageContext.request.contextPath}/webjars/bootstrap/5.1.3/js/bootstrap.bundle.min.js"></script>
</body>
</html>