<div class="row">
    <div class="col-12">
        <div class="jumbotron bg-light p-5 rounded mb-4">
            <h1 class="display-4">Welcome to Bookstore Management System!</h1>
            <p class="lead">Hello, <strong>${user.name}</strong>! You are logged in as
                <span class="badge bg-primary">${user.getClass().simpleName}</span>
            </p>
            <hr class="my-4">
            <p>Manage your bookstore operations efficiently with our comprehensive management system.</p>
        </div>
    </div>
</div>

<div class="row">
    <!-- Quick Stats -->
    <div class="col-md-3 mb-4">
        <div class="card text-white bg-primary">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h4 class="card-title">Users</h4>
                        <p class="card-text">Manage customers and employees</p>
                    </div>
                    <div class="align-self-center">
                        <i class="fas fa-users fa-2x"></i>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/users" class="btn btn-light btn-sm mt-2">Manage Users</a>
            </div>
        </div>
    </div>

    <div class="col-md-3 mb-4">
        <div class="card text-white bg-success">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h4 class="card-title">Orders</h4>
                        <p class="card-text">View customer orders</p>
                    </div>
                    <div class="align-self-center">
                        <i class="fas fa-shopping-cart fa-2x"></i>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/orders" class="btn btn-light btn-sm mt-2">View Orders</a>
            </div>
        </div>
    </div>

    <div class="col-md-3 mb-4">
        <div class="card text-white bg-warning">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h4 class="card-title">Supplies</h4>
                        <p class="card-text">Manage book supplies</p>
                    </div>
                    <div class="align-self-center">
                        <i class="fas fa-truck fa-2x"></i>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/supplies" class="btn btn-light btn-sm mt-2">Manage Supplies</a>
            </div>
        </div>
    </div>

    <div class="col-md-3 mb-4">
        <div class="card text-white bg-info">
            <div class="card-body">
                <div class="d-flex justify-content-between">
                    <div>
                        <h4 class="card-title">Profile</h4>
                        <p class="card-text">Your account info</p>
                    </div>
                    <div class="align-self-center">
                        <i class="fas fa-user fa-2x"></i>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/profile" class="btn btn-light btn-sm mt-2">View Profile</a>
            </div>
        </div>
    </div>
</div>

<!-- User-specific content based on role -->
<c:choose>
    <c:when test="${user.getClass().simpleName == 'Customer'}">
        <!-- Customer Dashboard -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header bg-success text-white">
                        <h5 class="mb-0"><i class="fas fa-shopping-bag me-2"></i>Customer Dashboard</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>Quick Actions:</h6>
                                <ul class="list-group">
                                    <li class="list-group-item">
                                        <a href="${pageContext.request.contextPath}/books" class="text-decoration-none">
                                            <i class="fas fa-book me-2"></i>Browse Books
                                        </a>
                                    </li>
                                    <li class="list-group-item">
                                        <a href="${pageContext.request.contextPath}/cart" class="text-decoration-none">
                                            <i class="fas fa-shopping-cart me-2"></i>View Cart
                                        </a>
                                    </li>
                                    <li class="list-group-item">
                                        <a href="${pageContext.request.contextPath}/my-orders" class="text-decoration-none">
                                            <i class="fas fa-list-alt me-2"></i>My Orders
                                        </a>
                                    </li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>Customer Features:</h6>
                                <ul>
                                    <li>Browse and search books</li>
                                    <li>Add books to cart</li>
                                    <li>Place orders</li>
                                    <li>View order history</li>
                                    <li>Leave reviews and ratings</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:when>

    <c:when test="${user.getClass().simpleName == 'Employee'}">
        <!-- Employee Dashboard -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header bg-warning text-dark">
                        <h5 class="mb-0"><i class="fas fa-briefcase me-2"></i>Employee Dashboard</h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>Management Tools:</h6>
                                <ul class="list-group">
                                    <li class="list-group-item">
                                        <a href="${pageContext.request.contextPath}/users" class="text-decoration-none">
                                            <i class="fas fa-user-cog me-2"></i>Manage Users
                                        </a>
                                    </li>
                                    <li class="list-group-item">
                                        <a href="${pageContext.request.contextPath}/supplies" class="text-decoration-none">
                                            <i class="fas fa-boxes me-2"></i>Manage Supplies
                                        </a>
                                    </li>
                                    <li class="list-group-item">
                                        <a href="${pageContext.request.contextPath}/discounts" class="text-decoration-none">
                                            <i class="fas fa-tag me-2"></i>Manage Discounts
                                        </a>
                                    </li>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6>Employee Features:</h6>
                                <ul>
                                    <li>Manage customer accounts</li>
                                    <li>Process book supplies</li>
                                    <li>Create and manage discounts</li>
                                    <li>View all orders</li>
                                    <li>Generate reports</li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:when>

    <c:otherwise>
        <!-- General User Dashboard -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header bg-secondary text-white">
                        <h5 class="mb-0"><i class="fas fa-user me-2"></i>User Dashboard</h5>
                    </div>
                    <div class="card-body">
                        <p>Welcome to your personal dashboard. Explore the available features using the navigation menu.</p>
                        <div class="row">
                            <div class="col-md-6">
                                <h6>Your Information:</h6>
                                <table class="table table-sm">
                                    <tr>
                                        <th>Name:</th>
                                        <td>${user.name}</td>
                                    </tr>
                                    <tr>
                                        <th>Email:</th>
                                        <td>${user.email}</td>
                                    </tr>
                                    <tr>
                                        <th>Phone:</th>
                                        <td>${user.phoneNumber}</td>
                                    </tr>
                                </table>
                            </div>
                            <div class="col-md-6">
                                <h6>Quick Links:</h6>
                                <div class="d-grid gap-2">
                                    <a href="${pageContext.request.contextPath}/profile" class="btn btn-outline-primary btn-sm">Edit Profile</a>
                                    <a href="${pageContext.request.contextPath}/books" class="btn btn-outline-success btn-sm">Browse Books</a>
                                    <a href="${pageContext.request.contextPath}/auth/logout" class="btn btn-outline-danger btn-sm">Logout</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:otherwise>
</c:choose>

<!-- Recent Activity Section -->
<div class="row mt-4">
    <div class="col-12">
        <div class="card">
            <div class="card-header">
                <h5 class="mb-0"><i class="fas fa-history me-2"></i>Recent Activity</h5>
            </div>
            <div class="card-body">
                <p class="text-muted">Recent activity features will be displayed here once implemented.</p>
                <ul class="list-group">
                    <li class="list-group-item">
                        <small class="text-muted">System ready for use</small>
                    </li>
                    <li class="list-group-item">
                        <small class="text-muted">Welcome to Bookstore Management System</small>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</div>

<!-- Add Font Awesome for icons (if not already included) -->
<style>
    .jumbotron {
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
    }
    .card {
        transition: transform 0.2s;
    }
    .card:hover {
        transform: translateY(-5px);
    }
</style>