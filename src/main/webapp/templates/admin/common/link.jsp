<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!-- Nav Item - Tables -->
<li class="nav-item ${ param.active ? 'active' : '' }">
    <a class="nav-link" href="${ param.link }">
        <i class="${ param.icon }"></i>
        <span>${ param.label }</span>
    </a>
</li>