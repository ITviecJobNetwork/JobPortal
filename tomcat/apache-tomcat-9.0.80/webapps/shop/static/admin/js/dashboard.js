const typeSelector = document.getElementById('type');

const PRIORITY_SELECTOR = {
    'YEAR': 1,
    'MONTH': 2,
}

const SELECTOR_ARRAY = [ document.getElementById('YEAR'), document.getElementById('MONTH') ]

typeSelector.addEventListener('change', e => {
    const { value } = e.target;

    const priority = PRIORITY_SELECTOR[value] ?? 0;

    for (let i = 0; i < priority; i++) {
        const selector = SELECTOR_ARRAY[i];
        selector.parentElement.classList.remove('d-none');
        selector.disabled = false;
    }

    for (let i = priority; i < SELECTOR_ARRAY.length; i++) {
        const selector = SELECTOR_ARRAY[i];
        selector.parentElement.classList.add('d-none');
        selector.disabled = true;
    }
})