function nuevoAjax(xmlhttp) {

    try {
        xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e) {
        try {
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        catch (E) {
            xmlhttp = false;
        }
    }
    if (!xmlhttp && typeof XMLHttpRequest != 'undefined') {
        xmlhttp = new XMLHttpRequest();
    }
    return xmlhttp
}

function cargarContenido(pagina, destino) {
    var load = "<img src='../img/loading.gif' align='center'>";

    $.ajax({
        url: pagina,
        type: 'GET',
        beforeSend: function () {
            // TODO: show your spinner
            $('#' + destino).empty().html($(load));
        },
        complete: function () {
            // TODO: hide your spinner
            $('#' + destino).remove('img');
        },
        success: function (result) {
            // TODO: handle the results
            console.log(result);
            $('#' + destino).empty().html(result);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            // TODO: handle the errors
            console.log(xhr.status, thrownError);
            $('#' + destino).empty().html(thrownError);
        }
    });
}

function cargarContenidoPost(pagina, destino) {

    $.ajax({
        url: pagina,
        type: 'POST',
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        beforeSend: function () {
            // TODO: show your spinners
            $('#' + destino).empty.html($(load));
        },
        complete: function () {
            // TODO: hide your spinner
            $('#' + destino).remove('img');
        },
        success: function (result) {
            // TODO: handle the results
            console.log(result);
            $('#' + destino).empty().html(result);
        },
        error: function (xhr, ajaxOptions, thrownError) {
            // TODO: handle the errors
            console.log(xhr.status, thrownError);
            $('#' + destino).empty().html(thrownError);
        }
    });
}

function getTimeForURL() {
    var dt = new Date();
    var strOutput = "";
    strOutput = dt.getHours() + "_" + dt.getMinutes() + "_" + dt.getSeconds() + "_" + dt.getMilliseconds();
    return strOutput;
}


function hideDiv(elm) {
    document.getElementById(elm).style.display = 'none';
}

function showDiv(elm) {
    document.getElementById(elm).style.display = 'block';
}

function GetRadioValue(radioObject) {
    var value = null;

    // Validate parameter value
//***	if (radioObject+"" == "undefined" || radioObject == null)
    if (radioObject == null)
        return null;

    for (var i = 0; i < radioObject.length; i++) {
        if (radioObject[i].checked) {
            value = radioObject[i].value;
            break;
        }
    } // end for loop

    return value;
}


function getSelectedText(elementId) {
    var elt = document.getElementById(elementId);

    if (elt.selectedIndex == -1)
        return '';

    return elt.options[elt.selectedIndex].text;
}

function Limpia_String(param) {
    param = param.replace("�", "a");
    param = param.replace("�", "e");
    param = param.replace("�", "i");
    param = param.replace("�", "o");
    param = param.replace("�", "u");
    param = param.replace("�", "n");
    return param;
}