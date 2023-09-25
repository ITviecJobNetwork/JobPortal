<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="shop__sidebar">
    <div class="shop__sidebar__search">
        <form method="get">
            <input type="text" placeholder="Search..." name="codeName" value="${ param.codeName }">
            <button type="submit"><span class="icon_search"></span></button>
        </form>
    </div>
    <div class="shop__sidebar__accordion">
        <div class="accordion" id="accordionExample">
            <div class="card">
                <div class="card-heading">
                    <a data-toggle="collapse" data-target="#collapseOne">Loại sản phẩm</a>
                </div>
                <div id="collapseOne" class="collapse show" data-parent="#accordionExample">
                    <div class="card-body">
                        <div class="shop__sidebar__categories">
                            <ul class="nice-scroll">
                                <c:forEach var="item" items="${ categories }">
                                    <li><a href="${ contextPath }/shopping?categoryId=${ item.id }">${ item.name }</a></li>
                                </c:forEach>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card">
                <div class="card-heading">
                    <a data-toggle="collapse" data-target="#collapseThree">Giá</a>
                </div>
                <div id="collapseThree" class="collapse show" data-parent="#accordionExample">
                    <div class="card-body">
                        <div class="shop__sidebar__price">
                            <ul>
                                <li><a href="${ contextPath }/shopping?minPrice=0&maxPrice=50000">0 - đ50.000</a></li>
                                <li><a href="${ contextPath }/shopping?minPrice=50000&maxPrice=100000">đ50.000 - đ100.000</a></li>
                                <li><a href="${ contextPath }/shopping?minPrice=100000&maxPrice=150000">đ100.000 - đ150.000</a></li>
                                <li><a href="${ contextPath }/shopping?minPrice=150000&maxPrice=200000">đ150.000 - đ200.000</a></li>
                                <li><a href="${ contextPath }/shopping?minPrice=200000&maxPrice=250000">đ200.000 - đ250.000</a></li>
                                <li><a href="${ contextPath }/shopping?minPrice=250000">đ250.000+</a></li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card">
                <div class="card-heading">
                    <a data-toggle="collapse" data-target="#collapseFour">Size</a>
                </div>
                <div id="collapseFour" class="collapse show" data-parent="#accordionExample">
                    <div class="card-body">
                        <div class="shop__sidebar__size">
                            <c:forEach var="item" items="${ sizes }">
                                <a href="${ contextPath }/shopping?size=${ item }">${ item }</a>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>