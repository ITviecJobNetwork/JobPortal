const columnController = document.getElementById('column-controller');
const columnOption = document.getElementById('column-option');
const columnHeader = document.querySelectorAll('.table-column-header');

// create option
columnHeader?.forEach(header => {
    const colName = header.dataset.col;
    const checked = header.dataset.show === 'true';
    const option = document.createElement('li');
    option.classList.add('border-bottom');

    const wrapper = document.createElement('div');
    wrapper.classList.add('p-2', 'd-flex', 'align-items-center');

    const checkbox = document.createElement('input');
    checkbox.type = 'checkbox';
    checkbox.checked = checked;
    if (!checked) {
        document.querySelectorAll(`[data-col=${colName}]`).forEach(col => {
            col.classList.add('d-none')
        })
    }
    checkbox.addEventListener('change', event => {
        document.querySelectorAll(`[data-col=${colName}]`).forEach(col => {
            col.classList.toggle('d-none');
        })
    })

    const label = document.createElement('span');
    label.textContent = header.textContent;
    label.classList.add('ml-2')

    wrapper.appendChild(checkbox);
    wrapper.appendChild(label);
    option.appendChild(wrapper);
    columnOption.appendChild(option);
})
columnController?.addEventListener('click', event => {
    columnOption.classList.toggle('d-block');
});