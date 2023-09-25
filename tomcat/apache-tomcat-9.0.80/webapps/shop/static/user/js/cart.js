"use-strict"
const checkboxCartSelector = document.getElementById('cart-selector');
const checkboxCartIds = document.querySelectorAll('input[type=checkbox][name=cartId]');
const originalPriceDOM = document.getElementById('original-price');
const discountDOM = document.getElementById('discount-price');
const totalPriceDOM = document.getElementById('total-price');
const quantityCartDOM = document.getElementById('quantity-cart');
const btnCheckoutDOM = document.getElementById('btn-checkout');

const checkboxCartIdsArr = Array.from(checkboxCartIds);

function countChecked() {
    return getCheckedCart().length;
}

function getCheckedCart() {
    return checkboxCartIdsArr.filter(element => element.checked);
}

function calculatePrice() {
    const checkedCart = getCheckedCart();
    const { newPrice, oldPrice } = checkedCart
        .map(cart => cart.dataset)
        .reduce(({ newPrice, oldPrice }, { newprice, oldprice, quantity }) => (
            { newPrice:  newPrice + (+newprice * quantity), oldPrice: oldPrice + (+oldprice * quantity) }
        ), { newPrice: 0, oldPrice: 0 })
    originalPriceDOM.textContent = $formatter.format(oldPrice);
    discountDOM.textContent = $formatter.format(oldPrice - newPrice);
    totalPriceDOM.textContent = $formatter.format(newPrice);
    quantityCartDOM.textContent = `${ checkedCart.length }`;
}

function setHrefBtnCheckout(checked) {
    if (checked) {
        btnCheckoutDOM.formAction = btnCheckoutDOM.dataset.href;
        btnCheckoutDOM.classList.remove('disabled');
    } else {
        btnCheckoutDOM.formAction = 'javascript:void(0)';
        btnCheckoutDOM.classList.add('disabled');
    }

}

(function main() {
    checkboxCartSelector.addEventListener('change', e => {
        setHrefBtnCheckout(e.target.checked);
        checkboxCartIds.forEach(element => {
            element.checked = e.target.checked;
        });
        calculatePrice();
    })

    checkboxCartIds.forEach(element => {
        element.addEventListener('change', e => {
            const checked = countChecked();
            const isCheckedAll = checked === checkboxCartIdsArr.length;
            checkboxCartSelector.checked = e.target.checked && isCheckedAll;
            calculatePrice();
            setHrefBtnCheckout(checked > 0);
        })
    })

    const proQty2 = $('.pro-qty-2');
    proQty2.prepend('<span class="fa fa-angle-left dec qtybtn"></span>');
    proQty2.append('<span class="fa fa-angle-right inc qtybtn"></span>');
    proQty2.on('click', '.qtybtn', function () {
        const $button = $(this);
        const oldValue = $button.parent().find('input').val();
        const inputQty = $button.parent().find('input');
        const max = inputQty.attr('max');
        if ($button.hasClass('inc')) {
            const newVal = parseFloat(oldValue) + 1;
            if (+max >= newVal) {
                inputQty.val(newVal);
            }
        } else {
            const newVal = parseFloat(oldValue) - 1;
            if (oldValue > 1) {
                inputQty.val(newVal);
            }
            if (newVal <= +max) {
                const tr = $button.parents('tbody > tr')[0];
                tr.classList.remove('border', 'border-warning');
            }
        }

    });
})()