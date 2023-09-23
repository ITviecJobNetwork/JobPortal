ClassicEditor
    .create(document.querySelector('#ckeditor'))
    .then(editor => {
        window.editor = editor;
    })
    .catch(err => {
        console.error(err.stack);
    });