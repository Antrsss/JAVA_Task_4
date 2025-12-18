<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="by.zgirskaya.course.util.AttributeParameter" %>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Error</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">
<div class="container mt-5">
  <div class="row justify-content-center">
    <div class="col-md-8 col-lg-6">
      <div class="card shadow">
        <div class="card-header bg-danger text-white">
          <h4 class="mb-0"><i class="bi bi-exclamation-triangle"></i> Error</h4>
        </div>
        <div class="card-body">
          <div class="text-center mb-4">
            <i class="bi bi-x-circle-fill text-danger" style="font-size: 4rem;"></i>
          </div>

          <h5 class="card-title text-center mb-4">
            <c:choose>
              <c:when test="${not empty requestScope[AttributeParameters.ERROR]}">
                ${requestScope[AttributeParameters.ERROR]}
              </c:when>
              <c:when test="${not empty param.error}">
                ${param.error}
              </c:when>
              <c:otherwise>
                An error occurred while processing your request.
              </c:otherwise>
            </c:choose>
          </h5>

          <div class="d-grid gap-2 d-md-flex justify-content-md-center mt-4">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary me-md-2">
              <i class="bi bi-house"></i> Home
            </a>
            <a href="javascript:history.back()" class="btn btn-outline-secondary">
              <i class="bi bi-arrow-left"></i> Go Back
            </a>
          </div>
        </div>
        <div class="card-footer text-muted text-center">
          <small>If the problem persists, please contact support.</small>
        </div>
      </div>
    </div>
  </div>
</div>
</body>
</html>