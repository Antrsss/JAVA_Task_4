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

        <div class="row">
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

        <div class="mb-3">
          <label for="supplyPrice" class="form-label">Price ($) <span class="text-danger">*</span></label>
          <div class="input-group">
            <span class="input-group-text">$</span>
            <input type="number" class="form-control" id="supplyPrice" name="supplyPrice"
                   value="${supply != null ? supply.supplyPrice : ''}"
                   step="0.01" min="0.01" placeholder="0.00" required>
            <div class="invalid-feedback">
              Please provide a valid price (minimum 0.01).
            </div>
          </div>
        </div>

        <div class="d-flex justify-content-end mt-4">
          <button type="submit" class="btn btn-success me-2" id="submitBtn">
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

<script src="${pageContext.request.contextPath}/js/supply-form-validation.js"></script>