<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<div class="orders-container">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1>Order History</h1>
    <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-primary">
      <i class="bi bi-cart"></i> View Cart
    </a>
  </div>

  <c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible fade show" role="alert">
        ${successMessage}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${error}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <div class="row mb-4">
    <div class="col-md-4">
      <div class="card text-white bg-primary">
        <div class="card-body">
          <h5 class="card-title">Total Orders</h5>
          <p class="card-text display-6">${orderCount}</p>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card text-white bg-success">
        <div class="card-body">
          <h5 class="card-title">Total Spent</h5>
          <p class="card-text display-6">
            <fmt:formatNumber value="${totalAmount}" type="currency" currencySymbol="$"/>
          </p>
        </div>
      </div>
    </div>
    <div class="col-md-4">
      <div class="card text-white bg-info">
        <div class="card-body">
          <h5 class="card-title">Average Order</h5>
          <p class="card-text display-6">
            <c:choose>
              <c:when test="${orderCount > 0}">
                <fmt:formatNumber value="${totalAmount / orderCount}" type="currency" currencySymbol="$"/>
              </c:when>
              <c:otherwise>$0.00</c:otherwise>
            </c:choose>
          </p>
        </div>
      </div>
    </div>
  </div>

  <c:choose>
    <c:when test="${empty orders}">
      <div class="alert alert-info">
        <i class="bi bi-clock-history"></i> You haven't placed any orders yet.
        <a href="${pageContext.request.contextPath}/books" class="alert-link">Start shopping</a>
      </div>
    </c:when>

    <c:otherwise>
      <div class="card">
        <div class="card-header bg-light">
          <h5 class="mb-0">Your Orders</h5>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
              <tr>
                <th>Order #</th>
                <th>Date</th>
                <th>Delivery Date</th>
                <th>Status</th>
                <th>Total</th>
                <th>Action</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach var="order" items="${orders}" varStatus="status">
                <tr>
                  <td>${status.index + 1}</td>
                  <td>
                    <fmt:formatDate value="${order.purchaseDate}" pattern="MMM dd, yyyy HH:mm"/>
                  </td>
                  <td>
                    <fmt:formatDate value="${order.deliveryDate}" pattern="MMM dd, yyyy"/>
                  </td>
                  <td>
                    <c:choose>
                      <c:when test="${order.orderStatus == 'COMPLETED'}">
                        <span class="badge bg-success">Completed</span>
                      </c:when>
                      <c:when test="${order.orderStatus == 'PROCESSING'}">
                        <span class="badge bg-warning">Processing</span>
                      </c:when>
                      <c:when test="${order.orderStatus == 'DELIVERED'}">
                        <span class="badge bg-info">Delivered</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge bg-secondary">${order.orderStatus}</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                  <td>
                    <fmt:formatNumber value="${order.orderPrice}" type="currency" currencySymbol="$"/>
                  </td>
                  <td>
                    <button class="btn btn-sm btn-outline-primary view-order-details"
                            data-order-id="${order.id}">
                      <i class="bi bi-eye"></i> Details
                    </button>
                  </td>
                </tr>
                <tr id="details-${order.id}" class="order-details" style="display: none;">
                  <td colspan="6">
                    <div class="p-3 bg-light rounded">
                      <h6>Order Details</h6>
                      <!-- Здесь можно добавить детали заказа через AJAX или другой способ -->
                      <p class="mb-0">Order ID: ${order.id}</p>
                      <p class="mb-0">Items will be loaded here...</p>
                    </div>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        // Обработка кликов по кнопкам просмотра деталей заказа
        document.querySelectorAll('.view-order-details').forEach(button => {
            button.addEventListener('click', function() {
                const orderId = this.getAttribute('data-order-id');
                const detailsRow = document.getElementById('details-' + orderId);

                if (detailsRow.style.display === 'none') {
                    detailsRow.style.display = 'table-row';
                    this.innerHTML = '<i class="bi bi-eye-slash"></i> Hide';

                    // Здесь можно добавить AJAX запрос для загрузки деталей заказа
                    // loadOrderDetails(orderId);
                } else {
                    detailsRow.style.display = 'none';
                    this.innerHTML = '<i class="bi bi-eye"></i> Details';
                }
            });
        });

        // Функция для загрузки деталей заказа (можно реализовать позже)
        function loadOrderDetails(orderId) {
            fetch('${pageContext.request.contextPath}/orders/' + orderId + '/items')
                .then(response => response.text())
                .then(html => {
                    document.querySelector('#details-' + orderId + ' .p-3').innerHTML = html;
                })
                .catch(error => console.error('Error loading order details:', error));
        }
    });
</script>