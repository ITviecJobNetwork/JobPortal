<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<section>
    <div class="container mt-3">
        <div class="row d-flex justify-content-center">
            <div class="col-md-12 col-lg-10">
                <div class="card text-dark">
                    <c:forEach var="item" items="${ product.comments }">
                        <div class="card-body p-4">
                            <div class="d-flex flex-start">
                                <img class="rounded-circle shadow-1-strong mr-3"
                                     src="${ pageContext.request.contextPath }/static/img/undraw_profile.svg"  alt="avatar" width="60"
                                     height="60" />
                                <div class="d-flex flex-column">
                                    <div>
                                        <div class="d-flex align-items-center mb-1">
                                            <h6 class="fw-bold font-weight-bold">${ item.fullName }</h6>
                                            <i>&lt;${ item.email }&gt;</i>
                                        </div>
                                        <div class="d-flex align-items-center mb-3">
                                            <p class="mb-0">
                                                <fmt:formatDate value="${ item.createdDate }" pattern="dd/MM/yyyy HH:mm" />
                                            </p>
                                        </div>
                                        <p class="mb-0">${ item.content }</p>
                                    </div>
                                    <c:forEach var="child" items="${ item.child }">
                                        <div class="d-flex flex-start mt-4">
                                            <a class="mr-3" href="#">
                                                <img class="rounded-circle shadow-1-strong mr-3"
                                                     src="${ pageContext.request.contextPath }/static/img/undraw_profile.svg"  alt="avatar" width="60"
                                                     height="60" />
                                            </a>
                                            <div class="flex-grow-1 flex-shrink-1">
                                                <div>
                                                    <c:if test="${ child.role ne 'ADMIN' }">
                                                        <div class="d-flex align-items-center mb-1">
                                                            <h6 class="fw-bold font-weight-bold">${ child.fullName }</h6>
                                                            <i>&lt;${ child.email }&gt;</i>
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${ child.role eq 'ADMIN' }">
                                                        <h6 class="fw-bold font-weight-bold">Chủ shop</h6>
                                                    </c:if>
                                                    <p class="small mb-0">${ child.content }</p>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </div>
                        <hr class="my-0" />
                    </c:forEach>
                    <div class="card-body p-4">
                        <div class="d-flex flex-start">
                            <jsp:include page="comment-form.jsp" />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>