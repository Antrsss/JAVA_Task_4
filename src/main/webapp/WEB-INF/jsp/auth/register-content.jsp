<div class="row justify-content-center">
    <div class="col-md-8">
        <div class="card">
            <div class="card-header">
                <h4 class="card-title mb-0">Register</h4>
            </div>
            <div class="card-body">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>

                <form action="${pageContext.request.contextPath}/auth/register" method="post">
                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="name" class="form-label">Full Name *</label>
                                <input type="text" class="form-control" id="name" name="name"
                                       value="${param.name}" required>
                            </div>

                            <div class="mb-3">
                                <label for="email" class="form-label">Email *</label>
                                <input type="email" class="form-control" id="email" name="email"
                                       value="${param.email}" required>
                            </div>
                        </div>

                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="password" class="form-label">Password *</label>
                                <input type="password" class="form-control" id="password" name="password" required>
                            </div>

                            <div class="mb-3">
                                <label for="phoneNumber" class="form-label">Phone Number</label>
                                <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber"
                                       value="${param.phoneNumber}">
                            </div>
                        </div>
                    </div>

                    <div class="row">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="role" class="form-label">Account Type *</label>
                                <select class="form-select" id="role" name="role" onchange="toggleFields()" required>
                                    <option value="">Select account type</option>
                                    <option value="customer" ${param.role == 'customer' ? 'selected' : ''}>Customer</option>
                                    <option value="employee" ${param.role == 'employee' ? 'selected' : ''}>Employee</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <!-- Customer fields -->
                    <div class="row" id="customerFields" style="display: none;">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="username" class="form-label">Username *</label>
                                <input type="text" class="form-control" id="username" name="username"
                                       value="${param.username}">
                            </div>
                        </div>
                    </div>

                    <!-- Employee fields -->
                    <div class="row" id="employeeFields" style="display: none;">
                        <div class="col-md-6">
                            <div class="mb-3">
                                <label for="passportId" class="form-label">Passport ID *</label>
                                <input type="text" class="form-control" id="passportId" name="passportId"
                                       value="${param.passportId}">
                            </div>
                        </div>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-success">Register</button>
                    </div>
                </form>

                <div class="text-center mt-3">
                    <p>Already have an account?
                        <a href="${pageContext.request.contextPath}/auth/login">Login here</a>
                    </p>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function toggleFields() {
        const role = document.getElementById('role').value;

        // Hide all fields first
        document.getElementById('customerFields').style.display = 'none';
        document.getElementById('employeeFields').style.display = 'none';

        // Remove required attributes
        document.getElementById('username').required = false;
        document.getElementById('passportId').required = false;

        // Show relevant fields and set required
        if (role === 'customer') {
            document.getElementById('customerFields').style.display = 'block';
            document.getElementById('username').required = true;
        } else if (role === 'employee') {
            document.getElementById('employeeFields').style.display = 'block';
            document.getElementById('passportId').required = true;
        }
    }

    // Initialize on page load
    toggleFields();
</script>