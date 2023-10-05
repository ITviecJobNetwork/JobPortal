const qtyDOM = document.getElementById('qty');
const newPriceDOM = document.getElementById('new-price');
const oldPriceDOM = document.getElementById('old-price');
const inputQtyDOM = document.getElementById('input-qty');
const radioOptionInputDOM = document.querySelectorAll('input[name^=option][type=radio]');
const inputIdDOM = document.getElementById('input-id');
const commentProductDOM = document.getElementById('comment-product');
const commentLengthDOM = document.getElementById('comment-length');
const btnAddCart = document.getElementById('btn-add-cart');

let preSelectedColor = null;

function getSelectedSizeColor() {
    const selectedColor = document.querySelector('input[name=optionColor][type=radio]:checked');
    const selectedSize = document.querySelector('input[name=optionSize][type=radio]:checked');
    return {
        selectedSizeColor: document.querySelector(`input[type=hidden]#${selectedColor.value}-${selectedSize.value}`),
        lblSelectedColor: document.querySelector(`label[for=${selectedColor.id}]`)
    };
}

function updateDetailInfo() {
    const { selectedSizeColor, lblSelectedColor } = getSelectedSizeColor();
    const { qty, price, discount: discountPercent, id, active } = selectedSizeColor.dataset;
    const newPrice = price * ( (100 - discountPercent) / 100 );
    if (preSelectedColor) preSelectedColor.classList.remove('disabled');
    if (active == 'false') {
        lblSelectedColor.classList.add('disabled');
        btnAddCart.disabled = true;
    } else {
        lblSelectedColor.classList.remove('disabled');
        btnAddCart.disabled = false;
    }
    if (newPrice == price) {
        oldPriceDOM.classList.add('d-none')
    } else {
        oldPriceDOM.classList.remove('d-none')
    }
    inputIdDOM.value = id;
    qtyDOM.textContent = qty;
    newPriceDOM.textContent = $formatter.format(+newPrice);
    oldPriceDOM.textContent = $formatter.format(+price);
    preSelectedColor = lblSelectedColor;
}

function quantityChange() {
    const proQty = $('.pro-qty');
    proQty.prepend('<span class="fa fa-angle-up inc qtybtn"></span>');
    proQty.append('<span class="fa fa-angle-down dec qtybtn"></span>');
    proQty.on('click', '.qtybtn', function () {
        const $button = $(this);
        const oldValue = inputQtyDOM.value;
        if ($button.hasClass('inc')) {
            const maxQuantity = +qtyDOM.textContent;
            if (maxQuantity > oldValue) {
                inputQtyDOM.value = +oldValue + 1;
            }
        } else {
            if (+oldValue > 1) {
                inputQtyDOM.value = +oldValue - 1;
            }
        }

    });
}

function updateCommentContentLength() {
    commentProductDOM?.addEventListener('keyup', e => {
        commentLengthDOM.textContent = e.target.value.length;
    })
}

(function main(){
    quantityChange();
    updateDetailInfo();
    updateCommentContentLength();
    radioOptionInputDOM.forEach(radioInput => {
        radioInput.addEventListener('change', updateDetailInfo)
    })
})()