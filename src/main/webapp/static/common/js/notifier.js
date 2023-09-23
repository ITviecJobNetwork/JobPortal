$.when( $.ready ).then(function() {
    const errorMessage = document.getElementById('_errorMessage');
    const successMessage = document.getElementById('_successMessage');
    const openModal = document.getElementById("_openModal");

    if (errorMessage) {
        Swal.fire({
            icon: 'error',
            title: 'Oops...',
            html: errorMessage.textContent
        })
    }

    if (successMessage) {
        Swal.fire({
            icon: 'success',
            title: 'Good',
            html: successMessage.textContent
        })
    }

    if (openModal) {
        $(openModal.dataset.val).modal('show');
    }
});