function _callEndpoint(method, endpoint, callback, body) {
    var xhttp = getXHttp(method, endpoint, callback);
    xhttp.setRequestHeader('Accept', 'application/json');
    xhttp.setRequestHeader('Content-type', 'application/json;charset=UTF-8');
    if(body) {
        xhttp.send(body);
    } else {
        xhttp.send();
    }
}

function _uploadFile(fileInputId, endpoint, callback) {
    var fileInput = document.getElementById(fileInputId);
    
    var files = fileInput.files;
    if (files.length === 0) {
        alert('Please select a file to upload');
        return;
    }
    
    var formData = new FormData();
    var fileInput = $("#"+fileInputId)[0];
    
    for (var i=0; i < files.length; i++) {
      formData.append('files', files[i]);
    }    
    
    var xhttp = getXHttp('POST', endpoint, callback);  
    xhttp.setRequestHeader('Accept', 'application/json'); 

    xhttp.send(formData);    
}

function getXHttp(method, endpoint, callback) {
    var xhttp = new XMLHttpRequest();
    
    xhttp.onreadystatechange = function() {
        if (this.readyState == 4) {
            if(this.status == 200) {
                return callback ? callback(this.responseText) : '';
                
            } else if(this.responseText && this.responseText !== '') {
                var errorResponse = JSON.parse(this.responseText);              
                handleEndpointError(errorResponse.error);
            }
        }
    };    
            
    xhttp.open(method, endpoint, false);     
    return xhttp;
}