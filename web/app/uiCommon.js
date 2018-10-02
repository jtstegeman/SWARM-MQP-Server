/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

window.swarmUi = {};

swarmApi.checkLoginInfo(function (data) {
    if (data.name) {
        $('.username').text(data.name);
    }
},
        function () {
            window.location = "/swarm/login.html"
        });

if ($('#devTable')) {
    $(document).ready(function () {
        swarmApi.getUserDevices(function (data) {
            if (data.data) {
                var dataSet = [];
                for (var i = 0; i < data.data.length; i++) {
                    dataSet[i] = ['<a href="#' + data.data[i].id + '" onclick="swarmUi.showDevice(' + data.data[i].id + ',\'' + data.data[i].key + '\',' + data.data[i].type + ')">' + data.data[i].id + '</a>', data.data[i].key, data.data[i].type, data.data[i].name, data.data[i].lat, data.data[i].lng];
                }
                $('#devTable').DataTable({
                    data: dataSet,
                    columns: [
                        {title: "Id"},
                        {title: "Key"},
                        {title: "Type"},
                        {title: "Name"},
                        {title: "Latitude"},
                        {title: "Longitude"}
                    ]
                });
            }
        });
    });
}

window.swarmUi.showDevice = function (deviceId, deviceKey, deviceType) {
    if ($('#devModal')) {
        $('#devModal').empty();
        $('#devModal').append('<div class="modal-dialog">' +
                '<div class="modal-content">' +
                '<div class="modal-header">' +
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
                '<h2 class="modal-title" id="myModalLabel">Device: ' + deviceId + '</h2>' +
                '</div>' +
                '<div class="modal-body" id="devModal-content">' +
                '</div>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                '</div>' +
                '</div>' +
                '</div>');
        $('#devModal').modal('show');
        swarmApi.getDevice(deviceId, deviceKey, deviceType, function (data) {
            console.log(data.data);
            $('#devModal-content').text(JSON.stringify(data.data));
        });
    }
}


if ($('#devMap')) {
    $(document).ready(function () {
        var map = L.map('devMap');
        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoianN0ZWciLCJhIjoiY2ptc2M3dmxyMDFmaTNrcndmYXd4djZtayJ9.TJmh8CuOCnJDeAEbmrw07A', {
            attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
            maxZoom: 22,
            id: 'mapbox.streets',
            accessToken: 'pk.eyJ1IjoianN0ZWciLCJhIjoiY2ptc2M3dmxyMDFmaTNrcndmYXd4djZtayJ9.TJmh8CuOCnJDeAEbmrw07A'
        }).addTo(map);

        swarmApi.getUserDevices(function (data) {
            if (data.data) {
                var aveLat = 0;
                var aveLng = 0;
                for (var i = 0; i < data.data.length; i++) {
                    var marker = L.marker([data.data[i].lat, data.data[i].lng]);
                    aveLat = aveLat + data.data[i].lat;
                    aveLng = aveLng + data.data[i].lng;
                    marker.bindPopup('<a href="#' + data.data[i].id + '" onclick="swarmUi.showDevice(' + data.data[i].id + ',\'' + data.data[i].key + '\',' + data.data[i].type + ')">Device: ' + data.data[i].id + '</a>');
                    marker.addTo(map);
                }
                if (data.data.length != 0) {
                    aveLat = aveLat / data.data.length;
                    aveLng = aveLng / data.data.length;
                }
                map.setView([aveLat, aveLng], 13);
            }
        });
    });
}