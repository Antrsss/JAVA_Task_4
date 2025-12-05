<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="java.util.Date" %>

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

    .btn-view {
        background: #667eea;
        color: white;
    }

    .btn-view:hover {
        background: #5a67d8;
        transform: translateY(-2px);
    }

    .btn-outline-secondary {
        background: transparent;
        color: #718096;
        border: 2px solid #718096;
    }

    .btn-outline-secondary:hover {
        background: #718096;
        color: white;
        transform: translateY(-2px);
    }

    .btn-warning {
        background: #ed8936;
        color: white;
    }

    .btn-warning:hover {
        background: #dd6b20;
        transform: translateY(-2px);
    }

    .btn-success {
        background: #48bb78;
        color: white;
    }

    .btn-success:hover {
        background: #38a169;
        transform: translateY(-2px);
    }

    .btn-danger {
        background: #f56565;
        color: white;
    }

    .btn-danger:hover {
        background: #e53e3e;
        transform: translateY(-2px);
    }

    .btn-info {
        background: #4299e1;
        color: white;
    }

    .btn-info:hover {
        background: #3182ce;
        transform: translateY(-2px);
    }

    .btn-sm {
        padding: 6px 12px;
        font-size: 0.875rem;
    }

    .cart-card {
        background: white;
        border-radius: 12px;
        overflow: hidden;
        transition: all 0.3s ease;
        box-shadow: 0 3px 10px rgba(0, 0, 0, 0.08);
        border: 1px solid #eaeaea;
        margin-bottom: 30px;
    }

    .cart-card:hover {
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
    }

    .cart-header {
        background: #f7fafc;
        padding: 20px;
        border-bottom: 1px solid #eaeaea;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .cart-header h2 {
        font-size: 1.5rem;
        color: #2d3748;
        margin: 0;
    }

    .badge {
        display: inline-block;
        padding: 6px 12px;
        font-size: 0.875rem;
        font-weight: 600;
        border-radius: 20px;
    }

    .badge-warning {
        background: #feebc8;
        color: #744210;
    }

    .cart-body {
        padding: 30px;
    }

    .cart-table {
        width: 100%;
        border-collapse: collapse;
        margin-bottom: 30px;
    }

    .cart-table th {
        text-align: left;
        padding: 15px;
        border-bottom: 2px solid #eaeaea;
        color: #4a5568;
        font-weight: 600;
        font-size: 1rem;
    }

    .cart-table td {
        padding: 20px 15px;
        border-bottom: 1px solid #eaeaea;
        vertical-align: middle;
    }

    .cart-table tr:hover {
        background-color: #fafafa;
    }

    .cart-table tfoot tr {
        background-color: #f7fafc;
    }

    .cart-table tfoot td {
        padding: 25px 15px;
        font-weight: 600;
        font-size: 1.1rem;
        color: #2d3748;
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

    .quantity-control {
        display: flex;
        align-items: center;
        gap: 10px;
    }

    .quantity-btn {
        width: 32px;
        height: 32px;
        border-radius: 6px;
        border: 1px solid #cbd5e0;
        background: white;
        color: #4a5568;
        font-size: 1rem;
        cursor: pointer;
        display: flex;
        align-items: center;
        justify-content: center;
        transition: all 0.2s ease;
    }

    .quantity-btn:hover:not(:disabled) {
        background: #f7fafc;
        border-color: #a0aec0;
    }

    .quantity-btn:disabled {
        opacity: 0.5;
        cursor: not-allowed;
    }

    .quantity-value {
        font-size: 1.1rem;
        font-weight: 600;
        min-width: 40px;
        text-align: center;
        color: #2d3748;
    }

    .price {
        font-size: 1.1rem;
        font-weight: 600;
        color: #2d3748;
    }

    .actions-group {
        display: flex;
        gap: 8px;
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

    .alert-info {
        background: #bee3f8;
        color: #2c5282;
        border: 1px solid #90cdf4;
    }

    .alert-dismissible {
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .close-btn {
        background: none;
        border: none;
        color: inherit;
        font-size: 1.2rem;
        cursor: pointer;
        opacity: 0.7;
        transition: opacity 0.2s ease;
    }

    .close-btn:hover {
        opacity: 1;
    }

    .alert-link {
        color: inherit;
        text-decoration: underline;
        font-weight: 600;
    }

    .cart-actions {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-top: 30px;
        padding-top: 30px;
        border-top: 1px solid #eaeaea;
    }

    .left-actions {
        display: flex;
        gap: 15px;
    }

    .checkout-form {
        display: flex;
        flex-direction: column;
        gap: 15px;
        max-width: 400px;
    }

    .input-group {
        display: flex;
        gap: 10px;
    }

    .form-control {
        padding: 10px 15px;
        border: 1px solid #cbd5e0;
        border-radius: 6px;
        font-size: 1rem;
        flex: 1;
        transition: border-color 0.2s ease;
    }

    .form-control:focus {
        outline: none;
        border-color: #667eea;
        box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .form-note {
        font-size: 0.875rem;
        color: #718096;
        margin-top: 5px;
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

    .text-muted {
        color: #718096;
    }

    .text-end {
        text-align: right;
    }

    .d-flex {
        display: flex;
    }

    .justify-content-between {
        justify-content: space-between;
    }

    .align-items-center {
        align-items: center;
    }

    .mb-4 {
        margin-bottom: 1.5rem;
    }

    .mt-1 {
        margin-top: 0.25rem;
    }

    .mt-4 {
        margin-top: 1.5rem;
    }

    .me-2 {
        margin-right: 0.5rem;
    }

    .ms-1 {
        margin-left: 0.25rem;
    }

    .mx-2 {
        margin-left: 0.5rem;
        margin-right: 0.5rem;
    }

    .flex-column {
        flex-direction: column;
    }

    .text-decoration-none {
        text-decoration: none;
    }

    .fw-bold {
        font-weight: 700;
    }

    .d-inline {
        display: inline;
    }
</style>
<div class="container">
  <div class="header">
    <h1>🛒 Your Shopping Cart</h1>
    <p>Review and manage your selected books</p>
  </div>

  <div class="stats-bar">
    <div class="total">
      Items in cart: <span>${items.size()}</span>
    </div>
    <div>
      <a href="${pageContext.request.contextPath}/orders" class="btn btn-outline-secondary">
        📋 View Order History
      </a>
    </div>
  </div>

  <c:if test="${not empty successMessage}">
    <div class="alert alert-success alert-dismissible">
      <span>✅ ${successMessage}</span>
      <button class="close-btn" onclick="this.parentElement.style.display='none'">×</button>
    </div>
  </c:if>

  <c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible">
      <span>❌ ${error}</span>
      <button class="close-btn" onclick="this.parentElement.style.display='none'">×</button>
    </div>
  </c:if>

  <c:choose>
    <c:when test="${empty cart or empty items}">
      <div class="cart-card">
        <div class="cart-body">
          <div class="empty-state">
            <h2>Your cart is empty</h2>
            <p>You haven't added any books to your cart yet.</p>
            <a href="${pageContext.request.contextPath}/books" class="btn btn-view" style="margin-top: 20px;">
              📚 Browse Books
            </a>
          </div>
        </div>
      </div>
    </c:when>

    <c:otherwise>
      <div class="cart-card">
        <div class="cart-header">
          <h2>Current Order</h2>
          <span class="badge badge-warning">In Cart</span>
        </div>
        <div class="cart-body">
          <table class="cart-table">
            <thead>
            <tr>
              <th>Book</th>
              <th>Quantity</th>
              <th>Unit Price</th>
              <th>Total</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="item" items="${items}">
              <tr>
                <td>
                  <div class="d-flex flex-column">
                    <a href="${pageContext.request.contextPath}/books/view/${item.bookId}"
                       class="book-link">
                      📖 View Book Details
                    </a>
                    <small class="book-id mt-1">
                      ID: ${item.bookId}
                    </small>
                  </div>
                </td>
                <td>
                  <div class="quantity-control">
                    <form method="post" action="${pageContext.request.contextPath}/cart"
                          class="d-inline" style="display: flex; align-items: center;">
                      <input type="hidden" name="action" value="updateQuantity">
                      <input type="hidden" name="itemId" value="${item.id}">
                      <input type="hidden" name="bookId" value="${item.bookId}">
                      <button type="submit" name="quantityChange" value="-1"
                              class="quantity-btn"
                              <c:if test="${item.quantity <= 1}">disabled</c:if>>
                        −
                      </button>
                      <span class="quantity-value">${item.quantity}</span>
                      <button type="submit" name="quantityChange" value="1"
                              class="quantity-btn">
                        +
                      </button>
                    </form>
                  </div>
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
                <td>
                  <div class="actions-group">
                    <form method="post" action="${pageContext.request.contextPath}/cart"
                          class="d-inline" onsubmit="return confirm('Remove this book from cart?')">
                      <input type="hidden" name="action" value="removeFromCart">
                      <input type="hidden" name="itemId" value="${item.id}">
                      <button type="submit" class="btn btn-danger btn-sm" title="Remove from cart">
                        🗑️
                      </button>
                    </form>

                    <a href="${pageContext.request.contextPath}/books/view/${item.bookId}"
                       class="btn btn-info btn-sm" title="View book details">
                      👁️
                    </a>
                  </div>
                </td>
              </tr>
            </c:forEach>
            </tbody>
            <tfoot>
            <tr>
              <td colspan="3" class="text-end"><strong>Order Total:</strong></td>
              <td colspan="2">
                <strong class="price">
                  <fmt:formatNumber value="${totalPrice}" type="currency" currencySymbol="$"/>
                </strong>
              </td>
            </tr>
            </tfoot>
          </table>

          <div class="cart-actions">
            <div class="left-actions">
              <form method="post" action="${pageContext.request.contextPath}/cart" class="d-inline">
                <input type="hidden" name="action" value="clearCart">
                <button type="submit" class="btn btn-warning"
                        onclick="return confirm('Clear your entire cart?')">
                  🗑️ Clear Cart
                </button>
              </form>

              <a href="${pageContext.request.contextPath}/books" class="btn btn-outline-secondary">
                ← Continue Shopping
              </a>
            </div>

            <div class="checkout-form">
              <!-- ЕДИНАЯ форма для оформления заказа -->
              <form method="post" action="${pageContext.request.contextPath}/cart" class="d-inline" style="width: 100%;">
                <div class="input-group">
                  <input type="date" name="deliveryDate" class="form-control"
                         min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 86400000)) %>"
                         required
                         value="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 86400000)) %>">
                  <input type="hidden" name="action" value="checkout">
                  <button type="submit" class="btn btn-success">
                    ✅ Checkout
                  </button>
                </div>
                <div class="form-note">
                  Minimum delivery date is tomorrow
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>

  <div class="footer">
    <p>&copy; 2024 Book Store. All rights reserved.</p>
  </div>
</div>

<script>
    // Устанавливаем минимальную дату доставки на завтра
    document.addEventListener('DOMContentLoaded', function() {
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        const dateInput = document.querySelector('input[name="deliveryDate"]');
        if (dateInput) {
            const formattedDate = tomorrow.toISOString().split('T')[0];
            dateInput.min = formattedDate;
            dateInput.value = formattedDate;
        }

        // Добавляем обработчики для кнопок изменения количества
        const quantityForms = document.querySelectorAll('form[action*="/cart"]');
        quantityForms.forEach(form => {
            const buttons = form.querySelectorAll('button[name="quantityChange"]');
            buttons.forEach(button => {
                button.addEventListener('click', function(e) {
                    const change = parseInt(this.value);
                    const quantitySpan = form.querySelector('.quantity-value');
                    let currentQuantity = parseInt(quantitySpan.textContent);

                    // Валидация минимального количества
                    if (change === -1 && currentQuantity <= 1) {
                        e.preventDefault();
                        return;
                    }
                });
            });
        });
    });
</script>
</body>
</html>