<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link href="${pageContext.request.contextPath}/static/css/home.css" rel="stylesheet">

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
                <h6>Shopping Management:</h6>
                <ul class="list-group">
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/books" class="text-decoration-none">
                      <i class="fas fa-book me-2"></i>Browse Books Catalog
                    </a>
                  </li>
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/cart" class="text-decoration-none">
                      <i class="fas fa-shopping-cart me-2"></i>View Shopping Cart
                      <span class="badge bg-primary float-end">0 items</span>
                    </a>
                  </li>
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/my-orders" class="text-decoration-none">
                      <i class="fas fa-list-alt me-2"></i>My Orders History
                    </a>
                  </li>
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/reviews" class="text-decoration-none">
                      <i class="fas fa-star me-2"></i>My Reviews & Ratings
                    </a>
                  </li>
                </ul>
              </div>
              <div class="col-md-6">
                <h6>Customer Features:</h6>
                <div class="list-group">
                  <div class="list-group-item">
                    <strong>Shopping Cart</strong>
                    <small class="d-block text-muted">Add books to cart with desired quantities</small>
                  </div>
                  <div class="list-group-item">
                    <strong>Order Management</strong>
                    <small class="d-block text-muted">Place orders and track delivery dates</small>
                  </div>
                  <div class="list-group-item">
                    <strong>Reviews & Ratings</strong>
                    <small class="d-block text-muted">Leave feedback and rate books you've purchased</small>
                  </div>
                  <div class="list-group-item">
                    <strong>Activity Log</strong>
                    <small class="d-block text-muted">All your actions are tracked in activity log</small>
                  </div>
                </div>
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
                    <a href="${pageContext.request.contextPath}/customers" class="text-decoration-none">
                      <i class="fas fa-user-cog me-2"></i>Manage Customers
                    </a>
                  </li>
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/supplies/new" class="text-decoration-none">
                      <i class="fas fa-boxes me-2"></i>Create New Supply Order
                    </a>
                  </li>
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/supplies" class="text-decoration-none">
                      <i class="fas fa-clipboard-list me-2"></i>View Supply Orders
                    </a>
                  </li>
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/discounts" class="text-decoration-none">
                      <i class="fas fa-tag me-2"></i>Manage Discounts
                    </a>
                  </li>
                  <li class="list-group-item">
                    <a href="${pageContext.request.contextPath}/activity-log" class="text-decoration-none">
                      <i class="fas fa-history me-2"></i>View Activity Log
                    </a>
                  </li>
                </ul>
              </div>
              <div class="col-md-6">
                <h6>Employee Features:</h6>
                <div class="list-group">
                  <div class="list-group-item">
                    <strong>Customer Management</strong>
                    <small class="d-block text-muted">Full CRUD operations on customer accounts</small>
                  </div>
                  <div class="list-group-item">
                    <strong>Supply Orders</strong>
                    <small class="d-block text-muted">Record book orders from publishers with quantities and costs</small>
                  </div>
                  <div class="list-group-item">
                    <strong>Discount Management</strong>
                    <small class="d-block text-muted">Create/modify/delete discounts for specific books or publishers</small>
                  </div>
                  <div class="list-group-item">
                    <strong>Supply Items</strong>
                    <small class="d-block text-muted">Track book ID, quantity, and total cost per supply item</small>
                  </div>
                  <div class="list-group-item">
                    <strong>Activity Tracking</strong>
                    <small class="d-block text-muted">All employee actions are logged for accountability</small>
                  </div>
                </div>
              </div>
            </div>

            <!-- Quick Actions for Employee -->
            <div class="row mt-4">
              <div class="col-12">
                <h6>Quick Actions:</h6>
                <div class="d-grid gap-2 d-md-flex">
                  <a href="${pageContext.request.contextPath}/customers/create" class="btn btn-outline-primary btn-sm">
                    <i class="fas fa-user-plus me-1"></i>Add New Customer
                  </a>
                  <a href="${pageContext.request.contextPath}/supplies/create" class="btn btn-outline-success btn-sm">
                    <i class="fas fa-truck-loading me-1"></i>Create Supply Order
                  </a>
                  <a href="${pageContext.request.contextPath}/discounts/create" class="btn btn-outline-warning btn-sm">
                    <i class="fas fa-percentage me-1"></i>Create Discount
                  </a>
                </div>
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
        <p class="text-muted">Your recent activities will be displayed here.</p>
        <ul class="list-group">
          <li class="list-group-item">
            <small class="text-muted">
              <i class="fas fa-sign-in-alt me-1"></i>
              You logged in successfully
            </small>
            <small class="text-muted float-end">Just now</small>
          </li>
          <li class="list-group-item">
            <small class="text-muted">
              <i class="fas fa-home me-1"></i>
              Welcome to Bookstore Management System
            </small>
            <small class="text-muted float-end">Today</small>
          </li>
        </ul>
      </div>
    </div>
  </div>
</div>