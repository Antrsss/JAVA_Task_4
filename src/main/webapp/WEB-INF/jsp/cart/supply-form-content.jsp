<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="by.zgirskaya.course.util.AttributeParameters" %>

<div class="supply-form-container">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1>${title}</h1>
    <a href="${pageContext.request.contextPath}/supplies" class="btn btn-outline-secondary">
      <i class="bi bi-arrow-left"></i> Back to Supplies
    </a>
  </div>

  <c:if test="${not empty requestScope[AttributeParameters.ERROR]}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${requestScope[AttributeParameters.ERROR]}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <c:if test="${action == 'create'}">
    <div class="alert alert-info mb-3">
      <i class="bi bi-info-circle"></i>
      This supply will be automatically assigned to you.
    </div>
  </c:if>

  <div class="card">
    <div class="card-body">
      <form method="post" action="${pageContext.request.contextPath}/supplies" class="needs-validation" novalidate
            id="supplyForm">
        <input type="hidden" name="action" value="${action}">

        <!-- Основная информация о поставке -->
        <div class="row mb-4">
          <div class="col-md-6 mb-3">
            <label for="publisherId" class="form-label">Publisher ID <span class="text-danger">*</span></label>
            <input type="text" class="form-control" id="publisherId" name="publisherId"
                   value="${supply != null ? supply.publisherId : ''}"
                   placeholder="Enter publisher UUID"
                   pattern="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
                   required>
            <div class="invalid-feedback">
              Please provide a valid publisher UUID (format: 00000000-0000-0000-0000-000000000000).
            </div>
          </div>

          <div class="col-md-6 mb-3">
            <label for="date" class="form-label">Date <span class="text-danger">*</span></label>
            <input type="date" class="form-control" id="date" name="date"
                   value="${supply != null ? supply.date : ''}"
                   required>
            <div class="invalid-feedback">
              Please select a valid date.
            </div>
          </div>
        </div>

        <!-- Секция для добавления книг -->
        <div class="card mb-4">
          <div class="card-header bg-light d-flex justify-content-between align-items-center">
            <h5 class="mb-0">Books in Supply</h5>
            <button type="button" class="btn btn-sm btn-success" id="addBookBtn">
              <i class="bi bi-plus-circle"></i> Add Book
            </button>
          </div>
          <div class="card-body">
            <div id="booksContainer">
              <!-- Сюда будут добавляться строки с книгами -->
              <div class="book-row row mb-3">
                <div class="col-md-4">
                  <label class="form-label">Book ID <span class="text-danger">*</span></label>
                  <input type="text" class="form-control book-id" name="bookIds[]"
                         placeholder="Book UUID"
                         pattern="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
                         required>
                </div>
                <div class="col-md-3">
                  <label class="form-label">Quantity <span class="text-danger">*</span></label>
                  <input type="number" class="form-control book-quantity" name="quantities[]"
                         min="1" value="1" required>
                </div>
                <div class="col-md-3">
                  <label class="form-label">Unit Price ($) <span class="text-danger">*</span></label>
                  <input type="number" class="form-control book-price" name="prices[]"
                         step="0.01" min="0.01" placeholder="0.00" required>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                  <button type="button" class="btn btn-sm btn-danger remove-book" disabled>
                    <i class="bi bi-trash"></i>
                  </button>
                </div>
              </div>
            </div>
            <div class="mt-3">
              <small class="text-muted">Total Books: <span id="totalBooks">1</span></small>
              <small class="text-muted ms-3">Total Quantity: <span id="totalQuantity">1</span></small>
              <small class="text-muted ms-3">Total Cost: $<span id="totalItemsCost">0.00</span></small>
            </div>
          </div>
        </div>

        <div class="d-flex justify-content-end mt-4">
          <button type="submit" class="btn btn-success me-2">
            <i class="bi bi-check-circle"></i>
            ${action == 'create' ? 'Create Supply' : 'Update Supply'}
          </button>
          <a href="${pageContext.request.contextPath}/supplies" class="btn btn-secondary">
            <i class="bi bi-x-circle"></i> Cancel
          </a>
        </div>
      </form>
    </div>
  </div>
</div>

<script>
    // Bootstrap form validation
    (function () {
        'use strict'

        var forms = document.querySelectorAll('.needs-validation')

        Array.prototype.slice.call(forms)
            .forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }

                    form.classList.add('was-validated')
                }, false)
            })
    })()

    // Dynamic books management
    document.addEventListener('DOMContentLoaded', function () {
        const booksContainer = document.getElementById('booksContainer');
        const addBookBtn = document.getElementById('addBookBtn');
        const totalBooksSpan = document.getElementById('totalBooks');
        const totalQuantitySpan = document.getElementById('totalQuantity');
        const totalItemsCostSpan = document.getElementById('totalItemsCost');

        let bookRowCount = 1;

        // Update totals function
        function updateTotals() {
            const bookRows = document.querySelectorAll('.book-row');
            let totalBooks = bookRows.length;
            let totalQuantity = 0;
            let totalItemsCost = 0;

            bookRows.forEach(row => {
                const quantityInput = row.querySelector('.book-quantity');
                const priceInput = row.querySelector('.book-price');

                const quantity = quantityInput ? parseInt(quantityInput.value) || 0 : 0;
                const price = priceInput ? parseFloat(priceInput.value) || 0 : 0;

                totalQuantity += quantity;
                totalItemsCost += quantity * price;
            });

            totalBooksSpan.textContent = totalBooks;
            totalQuantitySpan.textContent = totalQuantity;
            totalItemsCostSpan.textContent = totalItemsCost.toFixed(2);
        }

        // Add new book row
        if (addBookBtn) {
            addBookBtn.addEventListener('click', function () {
                bookRowCount++;
                const newRow = document.createElement('div');
                newRow.className = 'book-row row mb-3';
                newRow.innerHTML = `
          <div class="col-md-4">
            <label class="form-label">Book ID <span class="text-danger">*</span></label>
            <input type="text" class="form-control book-id" name="bookIds[]"
                   placeholder="Book UUID"
                   pattern="[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"
                   required>
          </div>
          <div class="col-md-3">
            <label class="form-label">Quantity <span class="text-danger">*</span></label>
            <input type="number" class="form-control book-quantity" name="quantities[]"
                   min="1" value="1" required>
          </div>
          <div class="col-md-3">
            <label class="form-label">Unit Price ($) <span class="text-danger">*</span></label>
            <input type="number" class="form-control book-price" name="prices[]"
                   step="0.01" min="0.01" placeholder="0.00" required>
          </div>
          <div class="col-md-2 d-flex align-items-end">
            <button type="button" class="btn btn-sm btn-danger remove-book">
              <i class="bi bi-trash"></i>
            </button>
          </div>
        `;

                if (booksContainer) {
                    booksContainer.appendChild(newRow);
                    updateTotals();

                    // Enable delete buttons for all rows except the first one
                    document.querySelectorAll('.remove-book').forEach((btn, index) => {
                        btn.disabled = index === 0;
                        btn.addEventListener('click', function () {
                            if (!btn.disabled && this.closest('.book-row')) {
                                this.closest('.book-row').remove();
                                updateTotals();
                            }
                        });
                    });
                }
            });
        }

        // Update totals when quantity or price changes
        if (booksContainer) {
            booksContainer.addEventListener('input', function (e) {
                if (e.target.classList.contains('book-quantity') ||
                    e.target.classList.contains('book-price')) {
                    updateTotals();
                }
            });
        }

        // Initialize
        updateTotals();
    });
</script>