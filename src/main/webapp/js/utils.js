/**
 * AJAX call management
 */

function makeCall(method, url, formElement, callback, reset = true) {
    let req = new XMLHttpRequest();
    req.onreadystatechange = function() {
        callback(req);
    };
    req.open(method, url);
    if (formElement == null) {
        req.send();
    } else {
        let formData = new FormData(formElement);
        let encodedData = new URLSearchParams(formData).toString();
        req.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        req.send(encodedData);
    }
    if (formElement !== null && reset === true) {
        formElement.reset();
    }
}
