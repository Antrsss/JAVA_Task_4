<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="by.zgirskaya.course.util.AttributeParameter" %>
<%@ page import="by.zgirskaya.course.util.AuthParameter" %>

<%
  String userRole = (String) session.getAttribute(AttributeParameter.USER_ROLE);
  boolean isEmployee = AuthParameter.Roles.EMPLOYEE.equals(userRole);
  request.setAttribute("isEmployee", isEmployee);
%>

<div class="supplies-container">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h1>Manage Supplies</h1>
    <c:if test="${isEmployee}">
      <a href="${pageContext.request.contextPath}/controller/supplies?action=new" class="btn btn-primary">
        <i class="bi bi-plus-circle"></i> Add New Supply
      </a>
    </c:if>
  </div>

  <c:if test="${not empty requestScope[AttributeParameters.ERROR]}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${requestScope[AttributeParameters.ERROR]}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <c:choose>
    <c:when test="${not empty requestScope[AttributeParameters.SUPPLIES]}">
      <div class="table-responsive">
        <table class="table table-striped table-hover">
          <thead class="table-dark">
          <tr>
            <th>ID</th>
            <th>Employee ID</th>
            <th>Publisher ID</th>
            <th>Date</th>
            <th>Price</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="supply" items="${requestScope[AttributeParameters.SUPPLIES]}">
            <tr>
              <td><code>${supply.id}</code></td>
              <td><code>${supply.employeeId}</code></td>
              <td><code>${supply.publisherId}</code></td>
              <td>${supply.date}</td>
              <td>
                <span class="badge bg-success">$${supply.supplyPrice}</span>
              </td>
              <td>
                <div class="btn-group btn-group-sm" role="group">
                  <c:if test="${isEmployee and sessionScope[AttributeParameters.USER].id == supply.employeeId}">
                    <a href="${pageContext.request.contextPath}/controller/supplies/delete/${supply.id}"
                       class="btn btn-danger"
                       onclick="return confirm('Are you sure you want to delete this supply?')">
                      <i class="bi bi-trash"></i> Delete
                    </a>
                  </c:if>
                  <c:if test="${not isEmployee or sessionScope[AttributeParameters.USER].id != supply.employeeId}">
                    <span class="text-muted">No actions available</span>
                  </c:if>
                </div>
              </td>
            </tr>
          </c:forEach>
          </tbody>
        </table>
      </div>
    </c:when>
    <c:otherwise>
      <div class="text-center py-5">
        <i class="bi bi-box-seam display-1 text-muted"></i>
        <h3 class="text-muted mt-3">No supplies found</h3>
        <p class="text-muted">There are no supplies in the system yet.</p>
        <c:if test="${isEmployee}">
          <a href="${pageContext.request.contextPath}/controller/supplies?action=new" class="btn btn-primary mt-3">
            <i class="bi bi-plus-circle"></i> Create First Supply
          </a>
        </c:if>
      </div>
    </c:otherwise>
  </c:choose>
</div>