<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="card ${ param.borderColorLeft } shadow h-100 py-2 ">
    <div class="card-body">
        <div class="row no-gutters align-items-center">
            <div class="col mr-2">
                <div class="text-xs font-weight-bold ${ param.titleColor } text-uppercase mb-1">
                    <a href="${ param._href }">${ param.title }</a>
                </div>
                <div class="h5 mb-0 font-weight-bold text-gray-800">${ param.value }</div>
            </div>
            <div class="col-auto">
                <i class="${ param.icon } fa-2x text-gray-300"></i>
            </div>
        </div>
    </div>
</div>