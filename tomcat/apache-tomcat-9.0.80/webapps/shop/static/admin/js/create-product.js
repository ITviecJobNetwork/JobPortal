const btnAddSize = document.getElementById('add-size-btn');
const sizeInput = document.getElementById('size-input');
const sizeContainer = document.getElementById('size-container');

const btnAddColor = document.getElementById('add-color-btn');
const colorInput = document.getElementById('color-input');
const colorContainer = document.getElementById('color-container');

const colorSizeBody = document.querySelector('#color-size-table tbody');
const formProduct = document.querySelector('form');
const colors = [];
const sizes = [];

btnAddSize?.addEventListener('click', e => {
    const valueSize = sizeInput.value;
    addSize(valueSize);
});

sizeInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        e.preventDefault();
        const valueSize = sizeInput.value;
        addSize(valueSize)
    }
})

btnAddColor?.addEventListener('click', e => {
    const valueColor = colorInput.value
    addColor(valueColor)
});

colorInput.addEventListener('keydown', e => {
    if (e.key === 'Enter') {
        e.preventDefault();
        const valueColor = colorInput.value;
        addColor(valueColor);
    }
});

formProduct.addEventListener('submit', e => {
    if (colors.length < 1 || sizes.length < 1) {
        e.preventDefault();
        return;
    }
    const colorsInput = document.getElementById('colors');
    const sizesInput = document.getElementById('sizes');
    colorsInput.value = colors.join(',');
    sizesInput.value = sizes.join(',');
})

function addSize(valueSize) {
    const size = sizes.find(size => size === valueSize);
    if (size) return;
    const sizeWrapper = createWrapper(sizeInput, removeSize);
    if (!sizeWrapper) return;
    sizes.push(sizeWrapper.textContent);
    sizeContainer.appendChild(sizeWrapper);
    colors.forEach(color => {
        const allTrColor = document.querySelectorAll(`tr.${color}`);
        if (allTrColor.length == 0) {
            const tdColor = createTdColor(color);
            sizes.forEach((size, index) => {
                const tr = createSizeRow(index, size, tdColor);
                colorSizeBody.appendChild(tr);
            });
            return;
        }
        const firstTr = allTrColor[0];
        const tdColor = firstTr.querySelector('td.color');
        tdColor.rowSpan = sizes.length;
        if (firstTr.classList.contains('empty-size')) {
            firstTr.classList.remove('empty-size');
            firstTr.classList.add(sizeWrapper.textContent);
            firstTr.querySelector('td.size').textContent = sizeWrapper.textContent;
            firstTr.querySelectorAll('input[name$=empty-size]').forEach(x => {
                x.setAttribute('name', x.getAttribute('name').replace('empty-size', sizeWrapper.textContent));
            });
        } else {
            const lastTr = allTrColor[allTrColor.length - 1];
            const newTrSize = createSizeRow(sizes.length, sizeWrapper.textContent, tdColor);
            const sibling = lastTr.nextSibling;
            if (sibling) {
                colorSizeBody.insertBefore(newTrSize, sibling);
            } else {
                colorSizeBody.appendChild(newTrSize);
            }
        }
    })
}

function removeSize(size) {
    const index = sizes.findIndex(s => s === size);
    sizes.splice(index, 1);
    document.querySelectorAll(`.${size}`).forEach(r => {
        const tdColor = r.querySelector('td.color');
        if (tdColor) {
            const sibling = r.nextSibling;
            if (sibling) {
                sibling.insertBefore(tdColor, sibling.childNodes[0]);
            }
        }
        r.remove();
    });
    document.querySelectorAll('td.color').forEach(t => t.rowSpan = sizes.length);
}

function addColor(valueColor) {
    const color = colors.find(color => color === valueColor);
    if (color) return;
    const wrapper = createWrapper(colorInput, removeColor);
    if (!wrapper) return;
    colors.push(wrapper.textContent);
    colorContainer.appendChild(wrapper);
    const tdColor = createTdColor(wrapper.textContent);
    if (!sizes.length) {
        const tr = createSizeRow(0, '', tdColor);
        colorSizeBody.appendChild(tr);
    } else {
        sizes.forEach((size, index) => {
            const tr = createSizeRow(index, size, tdColor);
            colorSizeBody.appendChild(tr);
        });
    }
}

function removeColor(color) {
    const index = colors.findIndex(c => c === color);
    colors.splice(index, 1);
    document.querySelectorAll(`.${color}`).forEach(t => t.remove());
}

function createWrapper(inputDom, removeCallback) {
    const value = inputDom.value;
    if (!value) return;
    const wrapper = document.createElement('div');
    wrapper.classList.add('card', 'px-3', 'py-2', 'delete', 'cursor-pointer');
    wrapper.textContent = value;
    wrapper.title = 'Click to delete';
    inputDom.value = null;

    wrapper.addEventListener('click', e => {
        e.target.parentNode.removeChild(wrapper);
        removeCallback(value);
    })
    return wrapper;
}

function createTdColor(color) {
    const tdColor = document.createElement('td');
    tdColor.rowSpan = sizes.length || 1;
    tdColor.dataset.name = color;
    tdColor.classList.add('color');
    const divColor = document.createElement('div');
    const labelColor = document.createElement('label');
    labelColor.textContent = color;

    const label = document.createElement('label');
    label.htmlFor = `color-${color}`;
    label.classList.add('d-flex', 'cursor-pointer');

    const iconUpload = document.createElement('i');
    iconUpload.classList.add('fa', 'fa-upload');
    label.appendChild(iconUpload);

    const upload = document.createElement('input');
    upload.type = 'file';
    upload.id = `color-${color}`;
    upload.classList.add('d-none');
    upload.name = `${color}`;
    upload.addEventListener('change', e => {
        if (!e.target?.files?.[0]) return;
        const image = label.getElementsByTagName('img');
        if (image?.[0]) label.removeChild(image[0]);
        const imagePreview = createPreviewImage(e.target.files[0]);
        iconUpload.classList.add('d-none');
        label.appendChild(imagePreview);
    })
    labelColor.appendChild(label);
    labelColor.appendChild(upload);
    divColor.appendChild(labelColor);
    tdColor.appendChild(divColor);
    return tdColor;
}

function createSizeRow(sizeIndex, size, tdColor) {
    const color = tdColor.dataset.name;
    const tr = document.createElement('tr');
    tr.classList.add(size || 'empty-size');
    tr.classList.add(color);
    if (sizeIndex === 0) {
        tr.appendChild(tdColor);
    }
    const tdSize = document.createElement('td');
    tdSize.textContent = size;
    tdSize.classList.add('size');
    tr.appendChild(tdSize);

    const tdQuantity = createTdInput((size || 'empty-size'), color, 'quantity');
    tr.appendChild(tdQuantity);

    const tdCost = createTdInput((size || 'empty-size'), color, 'cost');
    tr.appendChild(tdCost);

    const tdPrice = createTdInput((size || 'empty-size'), color, 'price');
    tr.appendChild(tdPrice);

    const tdDiscount = createTdInput((size || 'empty-size'), color, 'discount');
    tr.appendChild(tdDiscount);
    return tr;
}

function createTdInput(size, color, name) {
    const tdInput = document.createElement('td');
    const input = document.createElement('input');
    input.classList.add('form-control');
    input.type = 'number';
    input.value = '0';
    input.min = '0';
    input.name = `${name}-${color}-${size}`;
    addEventBlurInputNumber(input, 0);
    tdInput.appendChild(input);
    return tdInput;
}

function addEventBlurInputNumber(element, defaultValue) {
    element.addEventListener('blur', e => {
        const {value} = e.target;
        if (!value || +value < 0) e.target.value = defaultValue;
    })
}

function createPreviewImage(uploadFile) {
    const imagePreview = document.createElement('img');
    imagePreview.classList.add('preview-image', 'scalable')
    imagePreview.src = URL.createObjectURL(uploadFile);
    return imagePreview;
}

(function main() {
    const colorsInput = document.getElementById('colors');
    const sizesInput = document.getElementById('sizes');

    if (colorsInput.value) {
        const _colors = colorsInput.value?.split(',');
        colors.push(..._colors);
    }

    if (sizesInput.value) {
        const _sizes = sizesInput.value?.split(',');
        sizes.push(..._sizes);
    }

    document.querySelectorAll('#color-container > div').forEach(element => {
        element.addEventListener('click', e => {
            element.remove();
            removeColor(e.target.textContent);
        })
    });

    document.querySelectorAll('#size-container > div').forEach(element => {
        element.addEventListener('click', e => {
            element.remove();
            removeSize(e.target.textContent);
        })
    });

    document.querySelectorAll('input[id^=color]').forEach(element => {
        element.addEventListener('change', e => {
            if (!e.target?.files?.[0]) return;
            const parent = e.target.parentNode;
            const iconUpload = e.target.nextElementSibling;
            const image = parent.getElementsByTagName('img');
            if (image?.[0]) parent.removeChild(image[0]);
            const imagePreview = createPreviewImage(e.target.files[0]);
            iconUpload.classList.add('d-none');
            parent.appendChild(imagePreview);
        })
    });

    document.querySelectorAll('td > input[type=number]').forEach(element => {
        addEventBlurInputNumber(element, 0);
    })
})()