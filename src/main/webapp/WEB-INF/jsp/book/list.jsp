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

      .header p {
          font-size: 1.1rem;
          opacity: 0.9;
      }

      .book-grid {
          display: grid;
          grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
          gap: 25px;
          margin-top: 30px;
      }

      .book-card {
          background: white;
          border-radius: 12px;
          overflow: hidden;
          transition: all 0.3s ease;
          box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
          border: 1px solid #eaeaea;
      }

      .book-card:hover {
          transform: translateY(-5px);
          box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
      }

      .book-image {
          height: 200px;
          background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
          display: flex;
          align-items: center;
          justify-content: center;
          color: white;
          font-size: 4rem;
      }

      .book-content {
          padding: 20px;
      }

      .book-title {
          font-size: 1.3rem;
          font-weight: bold;
          color: #2d3748;
          margin-bottom: 10px;
          line-height: 1.4;
      }

      .book-price {
          font-size: 1.5rem;
          color: #667eea;
          font-weight: bold;
          margin-bottom: 10px;
      }

      .book-info {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 15px;
          padding-top: 10px;
          border-top: 1px solid #eaeaea;
      }

      .book-id {
          font-size: 0.85rem;
          color: #718096;
          font-family: monospace;
          background: #f7fafc;
          padding: 3px 8px;
          border-radius: 4px;
      }

      .stock-info {
          display: flex;
          align-items: center;
          gap: 8px;
      }

      .book-actions {
          display: flex;
          gap: 10px;
          margin-top: 15px;
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
          flex: 1;
      }

      .btn-view {
          background: #667eea;
          color: white;
      }

      .btn-view:hover {
          background: #5a67d8;
          transform: translateY(-2px);
      }

      .btn-add {
          background: #48bb78;
          color: white;
      }

      .btn-add:hover {
          background: #38a169;
          transform: translateY(-2px);
      }

      .btn-add:disabled {
          background: #cbd5e0;
          cursor: not-allowed;
          transform: none;
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

      .footer {
          text-align: center;
          margin-top: 50px;
          padding: 20px;
          color: #718096;
          font-size: 0.9rem;
          border-top: 1px solid #eaeaea;
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

      .total-books {
          font-size: 1.1rem;
          color: #4a5568;
      }

      .total-books span {
          font-weight: bold;
          color: #667eea;
      }
  </style>
</head>
<body>
<div class="container">
  <div class="header">
    <h1>📚 Book Catalog</h1>
    <p>Browse our collection of amazing books</p>
  </div>

  <div class="stats-bar">
    <div class="total-books">
      Total Books: <span>${books.size()}</span>
    </div>
    <div>
      <a href="${pageContext.request.contextPath}/controller/cart" class="btn btn-view">View Cart</a>
    </div>
  </div>

  <c:choose>
    <c:when test="${not empty books}">
      <div class="book-grid">
        <c:forEach items="${books}" var="book">
          <div class="book-card">
            <div class="book-image">
              📖
            </div>
            <div class="book-content">
              <h3 class="book-title">${book.title}</h3>
              <div class="book-price">
                <fmt:formatNumber value="${book.price}" type="currency" currencySymbol="$"/>
              </div>

              <div class="book-info">
                <div class="stock-info">
                  <div class="stock-indicator
                                            <c:choose>
                                                <c:when test="${book.quantity > 10}">in-stock</c:when>
                                                <c:when test="${book.quantity > 0}">low-stock</c:when>
                                                <c:otherwise>out-of-stock</c:otherwise>
                                            </c:choose>">
                  </div>
                  <span class="stock-text
                                            <c:choose>
                                                <c:when test="${book.quantity > 10}">quantity-high</c:when>
                                                <c:when test="${book.quantity > 0}">quantity-medium</c:when>
                                                <c:otherwise>quantity-low</c:otherwise>
                                            </c:choose>">
                                            <c:choose>
                                              <c:when test="${book.quantity > 10}">In Stock (${book.quantity})</c:when>
                                              <c:when test="${book.quantity > 0}">Low Stock (${book.quantity})</c:when>
                                              <c:otherwise>Out of Stock</c:otherwise>
                                            </c:choose>
                                        </span>
                </div>
                <div class="book-id">ID: ${book.id}</div>
              </div>

              <div class="book-actions">
                <a href="${pageContext.request.contextPath}/controller/books/view/${book.id}" class="btn btn-view">View Details</a>

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
        </c:forEach>
      </div>
    </c:when>
    <c:otherwise>
      <div class="empty-state">
        <h2>No Books Available</h2>
        <p>There are currently no books in our catalog. Please check back later.</p>
      </div>
    </c:otherwise>
  </c:choose>

  <div class="footer">
    <p>&copy; 2024 Book Store. All rights reserved.</p>
  </div>
</div>

<script src="${pageContext.request.contextPath}/static/js/add-to-cart.js"></script>
</body>
</html>