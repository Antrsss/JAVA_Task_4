<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="java.util.Date" %>

<div class="cart-container">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1>Your Shopping Cart</h1>
    <a href="${pageContext.request.contextPath}/orders" class="btn btn-outline-secondary">
      <i class="bi bi-clock-history"></i> View Order History
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

  <c:choose>
    <c:when test="${empty currentOrder or empty items}">
      <div class="alert alert-info">
        <i class="bi bi-cart"></i> Your cart is empty.
        <a href="${pageContext.request.contextPath}/books" class="alert-link">Browse books</a>
      </div>
    </c:when>

    <c:otherwise>
      <div class="card mb-4">
        <div class="card-header bg-light d-flex justify-content-between align-items-center">
          <h5 class="mb-0">Current Order</h5>
          <span class="badge bg-warning">In Cart</span>
        </div>
        <div class="card-body">
          <div class="table-responsive">
            <table class="table table-hover">
              <thead>
              <tr>
                <th>Book</th>
                <th>Quantity</th>
                <th>Unit Price</th>
                <th>Total</th>
                <th>Action</th>
              </tr>
              </thead>
              <tbody>
              <c:forEach var="item" items="${items}">
                <tr>
                  <td>
                    <c:if test="${item.book != null}">
                      ${item.book.title}
                    </c:if>
                    <c:if test="${item.book == null}">
                      <span class="text-muted">Book ID: ${item.bookId}</span>
                    </c:if>
                  </td>
                  <td>${item.quantity}</td>
                  <td>
                    <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="$"/>
                  </td>
                  <td>
                    <fmt:formatNumber value="${item.totalPrice}" type="currency" currencySymbol="$"/>
                  </td>
                  <td>
                    <form method="post" action="${pageContext.request.contextPath}/orders"
                          class="d-inline" onsubmit="return confirm('Remove this book from cart?')">
                      <input type="hidden" name="action" value="removeFromCart">
                      <input type="hidden" name="bookId" value="${item.bookId}">
                      <button type="submit" class="btn btn-sm btn-danger">
                        <i class="bi bi-trash"></i> Remove
                      </button>
                    </form>
                  </td>
                </tr>
              </c:forEach>
              </tbody>
              <tfoot>
              <tr class="table-active">
                <td colspan="3" class="text-end"><strong>Order Total:</strong></td>
                <td colspan="2">
                  <strong>
                    <fmt:formatNumber value="${totalPrice}" type="currency" currencySymbol="$"/>
                  </strong>
                </td>
              </tr>
              </tfoot>
            </table>
          </div>

          <div class="d-flex justify-content-between align-items-center mt-4">
            <div>
              <form method="post" action="${pageContext.request.contextPath}/orders" class="d-inline me-2">
                <input type="hidden" name="action" value="clearCart">
                <button type="submit" class="btn btn-warning"
                        onclick="return confirm('Clear your entire cart?')">
                  <i class="bi bi-trash"></i> Clear Cart
                </button>
              </form>
            </div>

            <div>
              <form method="post" action="${pageContext.request.contextPath}/orders" class="d-inline">
                <input type="hidden" name="action" value="checkout">
                <div class="input-group">
                  <input type="date" name="deliveryDate" class="form-control"
                         min="<%= new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() + 86400000)) %>"
                         required>
                  <button type="submit" class="btn btn-success">
                    <i class="bi bi-check-circle"></i> Checkout
                  </button>
                </div>
                <small class="text-muted">Minimum delivery date is tomorrow</small>
              </form>
            </div>
          </div>
        </div>
      </div>
    </c:otherwise>
  </c:choose>
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
    });
</script>