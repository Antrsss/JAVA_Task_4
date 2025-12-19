<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>${pageTitle} - Book Store</title>
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
          max-width: 800px;
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

      .back-link {
          display: inline-block;
          margin-bottom: 20px;
          color: #667eea;
          text-decoration: none;
          font-weight: 500;
      }

      .back-link:hover {
          text-decoration: underline;
      }

      .book-detail {
          background: white;
          border-radius: 12px;
          padding: 30px;
          box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
      }

      .book-header {
          display: flex;
          align-items: center;
          gap: 30px;
          margin-bottom: 30px;
      }

      .book-cover {
          width: 200px;
          height: 300px;
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 5rem;
          border-radius: 8px;
      }

      .book-info {
          flex: 1;
      }

      .book-title {
          font-size: 2rem;
          color: #2d3748;
          margin-bottom: 15px;
      }

      .book-price {
          font-size: 2rem;
          color: #667eea;
          font-weight: bold;
          margin-bottom: 20px;
      }

      .book-meta {
          display: grid;
          grid-template-columns: repeat(2, 1fr);
          gap: 15px;
          margin-bottom: 30px;
      }

      .meta-item {
          padding: 15px;
          background: #f7fafc;
          border-radius: 6px;
      }

      .meta-label {
          font-size: 0.9rem;
          color: #718096;
          margin-bottom: 5px;
      }

      .meta-value {
          font-size: 1.1rem;
          color: #2d3748;
          font-weight: 500;
      }

      .book-actions {
          display: flex;
          gap: 15px;
          margin-top: 30px;
      }

      .btn {
          padding: 12px 30px;
          border: none;
          border-radius: 6px;
          font-weight: 600;
          cursor: pointer;
          transition: all 0.2s ease;
          text-decoration: none;
          display: inline-block;
          text-align: center;
      }

      .btn-back {
          background: #718096;
          color: white;
      }

      .btn-add {
          background: #48bb78;
          color: white;
          flex: 1;
      }

      .btn-add:disabled {
          background: #cbd5e0;
          cursor: not-allowed;
      }
  </style>
</head>
<body>
<div class="container">
  <a href="${pageContext.request.contextPath}/controller/books" class="back-link">← Back to Catalog</a>

  <div class="header">
    <h1>Book Details</h1>
  </div>

  <div class="book-detail">
    <div class="book-header">
      <div class="book-cover">
        📖
      </div>
      <div class="book-info">
        <h1 class="book-title">${book.title}</h1>
        <div class="book-price">
          <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="$"/>
        </div>

        <div class="stock-status
                        <c:choose>
                            <c:when test="${book.quantity > 10}">in-stock</c:when>
                            <c:when test="${book.quantity > 0}">low-stock</c:when>
                            <c:otherwise>out-of-stock</c:otherwise>
                        </c:choose>">
          <c:choose>
            <c:when test="${book.quantity > 10}">
              ✅ In Stock (${book.quantity} available)
            </c:when>
            <c:when test="${book.quantity > 0}">
              ⚠ Low Stock (Only ${book.quantity} left!)
            </c:when>
            <c:otherwise>
              ❌ Out of Stock
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>

    <div class="book-meta">
      <div class="meta-item">
        <div class="meta-label">Book ID</div>
        <div class="meta-value">${book.id}</div>
      </div>
      <div class="meta-item">
        <div class="meta-label">Publisher ID</div>
        <div class="meta-value">${book.publisherId}</div>
      </div>
      <div class="meta-item">
        <div class="meta-label">Discount ID</div>
        <div class="meta-value">
          <c:choose>
            <c:when test="${not empty book.discountId}">${book.discountId}</c:when>
            <c:otherwise>No discount</c:otherwise>
          </c:choose>
        </div>
      </div>
      <div class="meta-item">
        <div class="meta-label">Available Quantity</div>
        <div class="meta-value">${book.quantity} units</div>
      </div>
    </div>

    <div class="book-actions">
      <a href="${pageContext.request.contextPath}/controller/books" class="btn btn-back">Back to Catalog</a>

      <form method="post" action="${pageContext.request.contextPath}/controller/cart" style="display: inline; flex: 1;">
        <input type="hidden" name="action" value="addToCart">
        <input type="hidden" name="bookId" value="${book.id}">
        <input type="hidden" name="quantity" value="1">
        <button type="submit" class="btn btn-add"
                <c:if test="${book.quantity == 0}">disabled</c:if>>
          Add to Cart
        </button>
      </form>
    </div>
  </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/add-to-cart.js"></script>
</body>
</html>