<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">

<div class="row justify-content-center">
  <div class="col-md-6">
    <div class="card">
      <div class="card-header">
        <h4 class="card-title mb-0">Register</h4>
      </div>
      <div class="card-body">
        <c:if test="${not empty error}">
          <div class="alert alert-danger">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/controller/auth/register" method="post">
          <div class="mb-3">
            <label for="name" class="form-label">Full Name *</label>
            <input type="text" class="form-control" id="name" name="name"
                   value="${param.name}" required>
          </div>

          <div class="mb-3">
            <label for="identifier" class="form-label">Phone Number or Email *</label>
            <input type="text" class="form-control" id="identifier" name="identifier"
                   value="${param.identifier}" placeholder="Enter your phone number or email address" required>
            <div class="form-text">This will be used for login. Provide either phone number or email.</div>
          </div>

          <div class="mb-3">
            <label for="password" class="form-label">Password *</label>
            <input type="password" class="form-control" id="password" name="password" required>
          </div>

          <div class="mb-3">
            <label class="form-label">Account Type *</label>
            <div>
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="role" id="roleCustomer"
                       value="Customer" ${param.role == 'Customer' ? 'checked' : ''} required>
                <label class="form-check-label" for="roleCustomer">Customer</label>
              </div>
              <div class="form-check form-check-inline">
                <input class="form-check-input" type="radio" name="role" id="roleEmployee"
                       value="Employee" ${param.role == 'Employee' ? 'checked' : ''}>
                <label class="form-check-label" for="roleEmployee">Employee</label>
              </div>
            </div>
          </div>

          <div class="mb-3" id="customerFields" style="display: none;">
            <label for="username" class="form-label">Username *</label>
            <input type="text" class="form-control" id="username" name="username"
                   value="${param.username}" placeholder="Enter your username">
          </div>

          <div class="mb-3" id="employeeFields" style="display: none;">
            <label for="passportId" class="form-label">Passport ID *</label>
            <input type="text" class="form-control" id="passportId" name="passportId"
                   value="${param.passportId}" placeholder="Enter your passport ID">
          </div>

          <div class="d-grid">
            <button type="submit" class="btn btn-primary">Register</button>
          </div>
        </form>

        <div class="text-center mt-3">
          <p>Already have an account?
            <a href="${pageContext.request.contextPath}/controller/auth/login">Login here</a>
          </p>
        </div>
      </div>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/register.js"></script>