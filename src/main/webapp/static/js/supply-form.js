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