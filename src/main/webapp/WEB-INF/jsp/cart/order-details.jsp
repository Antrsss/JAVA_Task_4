<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
  <title>Order Details</title>
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

      .breadcrumb {
          background: white;
          padding: 10px 20px;
          border-radius: 8px;
          margin-bottom: 20px;
          box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
          display: flex;
          align-items: center;
          gap: 10px;
      }

      .breadcrumb a {
          color: #667eea;
          text-decoration: none;
      }

      .breadcrumb a:hover {
          text-decoration: underline;
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

      .order-card {
          background: white;
          border-radius: 12px;
          overflow: hidden;
          box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
          border: 1px solid #eaeaea;
          margin-bottom: 30px;
      }

      .order-header {
          background: #f7fafc;
          padding: 25px;
          border-bottom: 1px solid #eaeaea;
      }

      .order-header h2 {
          font-size: 1.5rem;
          color: #2d3748;
          margin-bottom: 10px;
          display: flex;
          align-items: center;
          gap: 10px;
      }

      .order-id {
          font-family: monospace;
          font-size: 1rem;
          color: #718096;
          background: #edf2f7;
          padding: 5px 10px;
          border-radius: 4px;
      }

      .order-info {
          display: grid;
          grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
          gap: 20px;
          margin-top: 20px;
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
          font-size: 1.1rem;
          color: #2d3748;
          font-weight: 600;
      }

      .order-body {
          padding: 30px;
      }

      .section-title {
          font-size: 1.3rem;
          color: #2d3748;
          margin-bottom: 20px;
          padding-bottom: 10px;
          border-bottom: 2px solid #eaeaea;
      }

      .items-table {
          width: 100%;
          border-collapse: collapse;
          margin-bottom: 30px;
      }

      .items-table th {
          text-align: left;
          padding: 15px;
          border-bottom: 2px solid #eaeaea;
          color: #4a5568;
          font-weight: 600;
          font-size: 1rem;
          background: #f7fafc;
      }

      .items-table td {
          padding: 20px 15px;
          border-bottom: 1px solid #eaeaea;
          vertical-align: middle;
      }

      .items-table tr:hover {
          background-color: #fafafa;
      }

      .book-link {
          text-decoration: none;
          color: #667eea;
          font-weight: 600;
          display: flex;
          align-items: center;
          gap: 8px;
          transition: color 0.2s ease;
      }

      .book-link:hover {
          color: #5a67d8;
          text-decoration: underline;
      }

      .book-id {
          font-size: 0.85rem;
          color: #718096;
          font-family: monospace;
          background: #f7fafc;
          padding: 3px 8px;
          border-radius: 4px;
          display: inline-block;
          margin-top: 5px;
      }

      .quantity {
          font-weight: 600;
          color: #2d3748;
      }

      .price {
          font-weight: 600;
          color: #2d3748;
      }

      .total-row {
          background-color: #f7fafc;
          font-weight: bold;
      }

      .total-row td {
          padding: 25px 15px;
          font-size: 1.1rem;
      }

      .order-summary {
          background: #f7fafc;
          padding: 25px;
          border-radius: 8px;
          margin-top: 30px;
      }

      .summary-item {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding: 10px 0;
          border-bottom: 1px solid #eaeaea;
      }

      .summary-item:last-child {
          border-bottom: none;
          font-weight: bold;
          font-size: 1.2rem;
          color: #2d3748;
      }

      .alert {
          padding: 15px 20px;
          border-radius: 8px;
          margin-bottom: 20px;
          display: flex;
          align-items: center;
          gap: 12px;
      }

      .alert-danger {
          background: #fed7d7;
          color: #742a2a;
          border: 1px solid #fc8181;
      }

      .empty-state {
          text-align: center;
          padding: 40px 20px;
          color: #718096;
      }

      .empty-state h3 {
          font-size: 1.5rem;
          margin-bottom: 10px;
          color: #4a5568;
      }

      .footer {
          text-align: center;
          margin-top: 50px;
          padding: 20px;
          color: #718096;
          font-size: 0.9rem;
          border-top: 1px solid #eaeaea;
      }

      .actions-bar {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-top: 30px;
          padding-top: 30px;
          border-top: 1px solid #eaeaea;
      }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>📋 Order Details</h1>
    <p>View complete order information</p>
  </div>

  <div class="breadcrumb">
    <a href="${pageContext.request.contextPath}/controller/orders">← Back to Orders</a>
    <span>/</span>
    <span>Order Details</span>
  </div>

  <c:if test="${not empty error}">
    <div class="alert alert-danger">
      <span>❌ ${error}</span>
    </div>
  </c:if>

  <c:if test="${empty order}">
    <div class="order-card">
      <div class="empty-state">
        <h3>Order not found</h3>
        <p>The requested order could not be found.</p>
        <a href="${pageContext.request.contextPath}/controller/orders" class="btn btn-primary" style="margin-top: 20px;">
          ← Back to Orders
        </a>
      </div>
    </div>
  </c:if>

  <c:if test="${not empty order}">
    <div class="order-card">
      <div class="order-header">
        <h2>
          📦 Order Details
          <span class="order-id">#${fn:substring(order.id, 0, 8)}...</span>
        </h2>

        <div class="order-info">
          <div class="info-item">
            <span class="info-label">Order ID:</span>
            <span class="info-value">${order.id}</span>
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
      </div>

      <div class="order-body">
        <h3 class="section-title">📚 Order Items (${itemsCount})</h3>

        <c:choose>
          <c:when test="${empty orderItems}">
            <div class="empty-state">
              <h3>No items found in this order</h3>
            </div>
          </c:when>

          <c:otherwise>
            <table class="items-table">
              <thead>
              <tr>
                <th>Book</th>
                <th>Quantity</th>
                <th>Unit Price</th>
                <th>Total</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach var="item" items="${orderItems}">
                <tr>
                  <td>
                    <div style="display: flex; flex-direction: column;">
                      <a href="${pageContext.request.contextPath}/controller/books/view/${item.bookId}"
                         class="book-link">
                        📖 View Book Details
                      </a>
                      <small class="book-id">
                        ID: ${item.bookId}
                      </small>
                    </div>
                  </td>
                  <td>
                    <span class="quantity">${item.quantity}</span>
                  </td>
                  <td>
                                                <span class="price">
                                                    <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="$"/>
                                                </span>
                  </td>
                  <td>
                                                <span class="price">
                                                    <fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="$"/>
                                                </span>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
              <tfoot>
              <tr class="total-row">
                <td colspan="3" style="text-align: right;"><strong>Order Total:</strong></td>
                <td>
                  <strong class="price">
                    <fmt:formatNumber value="${order.orderPrice}" type="currency" currencySymbol="$"/>
                  </strong>
                </td>
              </tr>
              </tfoot>
            </table>
          </c:otherwise>
        </c:choose>

        <div class="order-summary">
          <div class="summary-item">
            <span>Number of Items:</span>
            <span>${itemsCount}</span>
          </div>
          <div class="summary-item">
            <span>Order Total:</span>
            <span><fmt:formatNumber value="${order.orderPrice}" type="currency" currencySymbol="$"/></span>
          </div>
        </div>

        <div class="actions-bar">
          <a href="${pageContext.request.contextPath}/controller/orders" class="btn btn-outline">
            ← Back to Orders
          </a>
          <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-primary">
            📚 Continue Shopping
          </a>
        </div>
      </div>
    </div>
  </c:if>

  <div class="footer">
    <p>&copy; 2024 Book Store. All rights reserved.</p>
  </div>
</div>
</body>
</html>