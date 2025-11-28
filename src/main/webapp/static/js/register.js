document.addEventListener('DOMContentLoaded', function() {
    const customerRadio = document.getElementById('roleCustomer');
    const employeeRadio = document.getElementById('roleEmployee');
    const customerFields = document.getElementById('customerFields');
    const employeeFields = document.getElementById('employeeFields');
    const usernameInput = document.getElementById('username');
    const passportInput = document.getElementById('passportId');

    function toggleFields() {
        if (customerRadio.checked) {
            customerFields.style.display = 'block';
            employeeFields.style.display = 'none';
            usernameInput.required = true;
            passportInput.required = false;
        } else if (employeeRadio.checked) {
            customerFields.style.display = 'none';
            employeeFields.style.display = 'block';
            usernameInput.required = false;
            passportInput.required = true;
        } else {
            customerFields.style.display = 'none';
            employeeFields.style.display = 'none';
            usernameInput.required = false;
            passportInput.required = false;
        }
    }

    customerRadio.addEventListener('change', toggleFields);
    employeeRadio.addEventListener('change', toggleFields);

    toggleFields();
});

document.getElementById('identifier').addEventListener('blur', function() {
    const identifier = this.value;
    const helpText = this.parentElement.querySelector('.form-text');

    if (identifier.includes('@')) {
        helpText.textContent = 'Email will be used as your login identifier';
        helpText.className = 'form-text text-info';
    } else if (identifier.trim() !== '') {
        helpText.textContent = 'Phone number will be used as your login identifier';
        helpText.className = 'form-text text-info';
    } else {
        helpText.textContent = 'This will be used for login. Provide either phone number or email.';
        helpText.className = 'form-text';
    }
});