<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
  <title>My Orders</title>
  <style>
      * {
          margin: 0;
          padding: 0;
          box-sizing: border-box;
      }

      body {
          font-family: 'Arial', sans-serif;
          line-height: 1.6;
          color: #333;
          background-color: #f5f5f5;
      }

      .container {
          max-width: 1200px;
          margin: 0 auto;
          padding: 20px;
      }

      .header {
          background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          color: white;
          padding: 30px 0;
          text-align: center;
          margin-bottom: 30px;
          border-radius: 10px;
          box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      }

      .header h1 {
          font-size: 2.5rem;
          margin-bottom: 10px;
      }

      .stats-bar {
          background: white;
          padding: 15px 20px;
          border-radius: 8px;
          margin-bottom: 20px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          display: flex;
          justify-content: space-between;
          align-items: center;
      }

      .stats-bar .total {
          font-size: 1.1rem;
          color: #4a5568;
      }

      .stats-bar .total span {
          font-weight: bold;
          color: #667eea;
      }

      .btn {
          padding: 10px 20px;
          border: none;
          border-radius: 6px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s ease;
          text-decoration: none;
          display: inline-block;
          text-align: center;
      }

      .btn-primary {
          background: #667eea;
          color: white;
      }

      .btn-primary:hover {
          background: #5a67d8;
          transform: translateY(-2px);
      }

      .btn-outline {
          background: transparent;
          color: #667eea;
          border: 2px solid #667eea;
      }

      .btn-outline:hover {
          background: #667eea;
          color: white;
          transform: translateY(-2px);
      }

      .btn-sm {
          padding: 6px 12px;
          font-size: 0.875rem;
      }

      .order-card {
          background: white;
          border-radius: 12px;
          overflow: hidden;
          transition: all 0.3s ease;
          box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
          border: 1px solid #eaeaea;
          margin-bottom: 20px;
      }

      .order-card:hover {
          box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
      }

      .order-header {
          background: #f7fafc;
          padding: 20px;
          border-bottom: 1px solid #eaeaea;
          display: flex;
          justify-content: space-between;
          align-items: center;
          cursor: pointer;
      }

      .order-header h3 {
          font-size: 1.2rem;
          color: #2d3748;
          margin: 0;
          display: flex;
          align-items: center;
          gap: 10px;
      }

      .order-id {
          font-family: monospace;
          font-size: 0.9rem;
          color: #718096;
          background: #edf2f7;
          padding: 2px 8px;
          border-radius: 4px;
      }

      .order-date {
          font-size: 0.9rem;
          color: #718096;
      }

      .order-price {
          font-size: 1.2rem;
          font-weight: bold;
          color: #48bb78;
      }

      .order-body {
          padding: 20px;
          display: none;
          background: #fafafa;
      }

      .order-body.expanded {
          display: block;
      }

      .order-info {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
          gap: 15px;
          margin-bottom: 20px;
      }

      .info-item {
          display: flex;
          flex-direction: column;
          gap: 5px;
      }

      .info-label {
          font-size: 0.875rem;
          color: #718096;
          font-weight: 500;
      }

      .info-value {
          font-size: 1rem;
          color: #2d3748;
          font-weight: 600;
      }

      .items-table th {
          text-align: left;
          padding: 12px 15px;
          border-bottom: 2px solid #eaeaea;
          color: #4a5568;
          font-weight: 600;
          font-size: 1rem;
          background: #f7fafc;
      }

      .items-table td {
          padding: 15px;
          border-bottom: 1px solid #eaeaea;
          vertical-align: middle;
      }

      .items-table tr:hover {
          background-color: #fafafa;
      }

      .empty-state {
          text-align: center;
          padding: 60px 20px;
          color: #718096;
      }

      .empty-state h2 {
          font-size: 1.8rem;
          margin-bottom: 10px;
          color: #4a5568;
      }

      .alert {
          padding: 15px 20px;
          border-radius: 8px;
          margin-bottom: 20px;
          display: flex;
          align-items: center;
          gap: 12px;
      }

      .alert-success {
          background: #c6f6d5;
          color: #22543d;
          border: 1px solid #9ae6b4;
      }

      .alert-danger {
          background: #fed7d7;
          color: #742a2a;
          border: 1px solid #fc8181;
      }

      .actions {
          display: flex;
          gap: 10px;
          margin-top: 10px;
      }

      .footer {
          text-align: center;
          margin-top: 50px;
          padding: 20px;
          color: #718096;
          font-size: 0.9rem;
          border-top: 1px solid #eaeaea;
      }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>📋 My Orders</h1>
    <p>View your order history and details</p>
  </div>

  <div class="stats-bar">
    <div class="total">
      Total Orders: <span>${orderCount}</span>
    </div>
    <div>
      <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-outline">
        ← Continue Shopping
      </a>
    </div>
  </div>

  <c:if test="${not empty successMessage}">
    <div class="alert alert-success">
      <span>✅ ${successMessage}</span>
    </div>
  </c:if>

  <c:if test="${not empty error}">
    <div class="alert alert-danger">
      <span>❌ ${error}</span>
    </div>
  </c:if>

  <c:choose>
    <c:when test="${empty orders or orderCount == 0}">
      <div class="order-card">
        <div class="empty-state">
          <h2>No orders yet</h2>
          <p>You haven't placed any orders yet.</p>
          <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-primary" style="margin-top: 20px;">
            📚 Browse Books
          </a>
        </div>
      </div>
    </c:when>

    <c:otherwise>
      <c:forEach var="order" items="${orders}">
        <div class="order-card" id="order-${order.id}">
          <div class="order-header" onclick="toggleOrderDetails('${order.id}')">
            <div>
              <h3>
                📦 Order
                <span class="order-id">#${fn:substring(order.id, 0, 8)}...</span>
              </h3>
              <div class="order-date">
                <fmt:formatDate value="${order.purchaseDate}" pattern="dd.MM.yyyy HH:mm"/>
              </div>
            </div>
            <div class="order-price">
              <fmt:formatNumber value="${order.orderPrice}" type="currency" currencySymbol="$"/>
            </div>
          </div>

          <div class="order-body" id="details-${order.id}">
            <div class="order-info">
              <div class="info-item">
                <span class="info-label">Order ID:</span>
                <span class="info-value order-id">${order.id}</span>
              </div>
              <div class="info-item">
                <span class="info-label">Order Date:</span>
                <span class="info-value">
                                        <fmt:formatDate value="${order.purchaseDate}" pattern="dd.MM.yyyy HH:mm:ss"/>
                                    </span>
              </div>
              <div class="info-item">
                <span class="info-label">Delivery Date:</span>
                <span class="info-value">
                                        <fmt:formatDate value="${order.deliveryDate}" pattern="dd.MM.yyyy"/>
                                    </span>
              </div>
              <div class="info-item">
                <span class="info-label">Status:</span>
                <span class="info-value" style="color: #48bb78;">Completed</span>
              </div>
            </div>

            <div class="actions">
              <a href="${pageContext.request.contextPath}/controller/orders/view/${order.id}"
                 class="btn btn-primary btn-sm">
                👁️ View Full Details
              </a>
            </div>
          </div>
        </div>
      </c:forEach>
    </c:otherwise>
  </c:choose>

  <div class="footer">
    <p>&copy; 2024 Book Store. All rights reserved.</p>
  </div>
</div>

<script>
    function toggleOrderDetails(orderId) {
        const details = document.getElementById('details-' + orderId);
        const orderCard = document.getElementById('order-' + orderId);

        details.classList.toggle('expanded');

        document.querySelectorAll('.order-body.expanded').forEach(expanded => {
            if (expanded.id !== 'details-' + orderId) {
                expanded.classList.remove('expanded');
            }
        });
    }

    document.addEventListener('DOMContentLoaded', function() {
        const firstOrder = document.querySelector('.order-card');
        if (firstOrder) {
            const firstOrderId = firstOrder.id.split('-')[1];
            toggleOrderDetails(firstOrderId);
        }
    });
</script>
</body>
</html>