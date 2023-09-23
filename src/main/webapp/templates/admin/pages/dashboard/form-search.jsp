<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<form id="form-statistic" class="col-md-12" data-uriRest="${ contextPath }/admin/rest/dashboard">
    <div class="row">
        <div class="col-md-3">
            <select id="type" class="form-control" name="type">
                <option value="${ null }">Chọn loại TG</option>
                <option value="MONTH">Tháng</option>
                <option value="YEAR">Năm</option>
            </select>
        </div>

        <div class="col-md-3 d-none">
            <select id="MONTH" class="form-control" name="month" required disabled>
                <option value="${ null }">-- Chọn tháng --</option>
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
                <option value="6">6</option>
                <option value="7">7</option>
                <option value="8">8</option>
                <option value="9">9</option>
                <option value="10">10</option>
                <option value="11">11</option>
                <option value="12">12</option>
            </select>
        </div>

        <div class="col-md-3 d-none">
            <input id="YEAR" type="number" class="form-control" name="year" placeholder="Nhập năm" required disabled/>
        </div>

        <div class="col-md-2">
            <button class="btn btn-primary">
                <i class="fa fa-line-chart mr-2"></i>
                Thống kê
            </button>
        </div>
    </div>
</form>