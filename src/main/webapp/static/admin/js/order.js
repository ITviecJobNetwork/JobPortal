const showDetailOrders = document.querySelectorAll('.show-detail-order');
const orderDetailContainer = document.getElementById('order-detail');
const tbody = orderDetailContainer.querySelector('tbody');

showDetailOrders?.forEach(showDetailOrder => {
    showDetailOrder.addEventListener('click', e => {
        const orderId = e.target.dataset.orderid;
        const urlData = e.target.dataset.urldata;
        showSpinner();
        $.ajax({
            url: urlData + '?orderId=' + orderId,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                const html = data.orderDetails?.map((od, index) => (`
                    <tr>
                        <td>${ index + 1 }</td>
                        <td>
                            <img src="${ od.productImg }" style="width: 100%; height: 100px; max-width: 200px"/>
                        </td>
                        <td>${ od.productCode }</td>
                        <td>${ od.productName }</td>
                        <td>${ od.color }</td>
                        <td>${ od.size }</td>
                        <td>${ od.quantity }</td>
                        <td>${ $formatter.format(od.cost) }</td>
                        <td>${ $formatter.format(od.price) }</td>
                        <td>${ od.discount ?? 0 }</td>
                        <td>${ $formatter.format( (od.price * od.quantity) * ( 100 - (od.discount ?? 0) ) / 100 ) }</td>
                    </tr>
                `)).join('');
                tbody.setHTML(html);
                $('#order-detail').modal('show');
            },
            complete: function() {
                hideSpinner();
            }
        })
    })
})