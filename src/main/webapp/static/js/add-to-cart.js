document.addEventListener('DOMContentLoaded', function() {
    const addToCartForm = document.querySelector('form[action*="/cart"]');
    if (addToCartForm) {
        addToCartForm.addEventListener('submit', function(e) {
            if (this.querySelector('button[disabled]')) {
                e.preventDefault();
                alert('This book is out of stock');
            }
        });
    }
});