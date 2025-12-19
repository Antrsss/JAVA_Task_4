<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">

<div class="row justify-content-center">
  <div class="col-md-6">
    <div class="card">
      <div class="card-header">
        <h4 class="card-title mb-0">Login</h4>
      </div>
      <div class="card-body">
        <c:if test="${not empty error}">
          <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/controller/auth/login" method="post">
          <div class="mb-3">
            <label for="identifier" class="form-label">Phone Number or Email *</label>
            <input type="text" class="form-control" id="identifier" name="identifier"
                   value="${param.identifier}" placeholder="Enter your phone number or email" required>
            <div class="form-text">You can use your phone number or email address to login</div>
          </div>

          <div class="mb-3">
            <label for="password" class="form-label">Password *</label>
            <input type="password" class="form-control" id="password" name="password" required>
          </div>

          <div class="d-grid">
            <button type="submit" class="btn btn-primary">Login</button>
          </div>
        </form>

        <div class="text-center mt-3">
          <p>Don't have an account?
            <a href="${pageContext.request.contextPath}/controller/auth/register">Register here</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</div>