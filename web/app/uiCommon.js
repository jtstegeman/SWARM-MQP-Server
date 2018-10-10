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
            $('#devModal-content').empty();
            $('#devModal-content').append('<button style="float:right;" id="devModal-delete"><i class="fa fa-trash"></i></button>');
            $('#devModal-content').append('<button style="float:right;" id="devModal-save"><i class="fa fa-save"></i></button>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Id: </span>' + data.data.id + '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Key: </span><input id="devModal-key" style="border:none;" type="text" value="' + data.data.key + '"> </div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Type: </span><input id="devModal-type" style="border:none;" type="text" value="' + data.data.type + '"></div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Name: </span><input id="devModal-name" style="border:none;" type="text" value="' + data.data.name + '"></div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Location: </span>(<input id="devModal-lat" style="border:none;" type="text" value="' + data.data.lat + '">, <input id="devModal-lng" style="border:none;" type="text" value="' + data.data.lng + '">)</div>');
            for (x in data.data.nums) {
                $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;">' + x + ': </span>' + data.data.nums[x] + '</div>');
            }
            for (x in data.data.strs) {
                $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;">' + x + ': </span>' + data.data.strs[x] + '</div>');
            }
            $('#devModal-content').append('<br><br><div><span style="display: inline-block; width: 30%; font-size: 1.5em;">History:</span><button style="float:right;" id="devModal-history-refresh"><i class="fa fa-refresh"></i></button></div>');
            $('#devModal-content').append('<div>Since: <input style="line-height: inherit; !important" type="date" id="devModal-history-since"> ' +
                    'Before: <input style="line-height: inherit; !important" type="date" id="devModal-history-before"></div>');
            $('#devModal-content').append('<div id="devModal-history-graph"></div>');
            $('#devModal-history-before').val(new Date().toISOString().substr(0, 10));
            $('#devModal-history-since').val(new Date(new Date().getTime() - (60 * 60 * 24 * 7 * 1000)).toISOString().substr(0, 10));
            $('#devModal-save').click(function () {
                swarmApi.updateDevice(data.data.id, data.data.key, $('#devModal-key').val(), $('#devModal-type').val(), $('#devModal-name').val(), $('#devModal-lat').val(), $('#devModal-lng').val(), function (res) {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
            });
            
            $('#devModal-delete').click(function () {
                swarmApi.deleteDevice(data.data.id, data.data.key, function (res) {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
                setTimeout(function(){window.location.reload()},2000);
            });

            $('#devModal-history-refresh').click(function () {
                swarmApi.getDeviceHistory(data.data.id, data.data.key, new Date($('#devModal-history-since').val()).getTime(), new Date($('#devModal-history-before').val()).getTime(), function (res) {
                    $('#devModal-history-graph').empty();
                    if (res.data.length == 0) {
                        $('#devModal-history-graph').text('No Data');
                    } else {
                        $('#devModal-history-graph').css('width', '100%');
                        $('#devModal-history-graph').css('height', '500px');
                        var gData = [];
                        for (x in data.data.nums) {
                            var t1 = res.data.filter(function (tm) {
                                return tm.state != undefined && tm.state.nums != undefined && tm.state.nums[x] != undefined;
                            });
                            var gEDat = {
                                y: t1.map(function (e) {
                                    return e.state.nums[x]
                                }),
                                x: t1.map(function (e) {
                                    return new Date(e.activity).toISOString()
                                }),
                                mode: 'lines+markers',
                                name: x
                            };
                            gData.push(gEDat);
                        }
                        for (x in data.data.strs) {
                            var t1 = res.data.filter(function (tm) {
                                return tm.state != undefined && tm.state.strs != undefined && tm.state.strs[x] != undefined;
                            });
                            var gEDat = {
                                y: t1.map(function (e) {
                                    return e.state.strs[x]
                                }),
                                x: t1.map(function (e) {
                                    return new Date(e.activity).toISOString()
                                }),
                                mode: 'lines+markers',
                                name: x
                            };
                            gData.push(gEDat);
                        }
                        Plotly.newPlot('devModal-history-graph', gData);
                    }
                })
            });
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

if ($('#newDevModal')) {
    $(document).ready(function () {
        $('#btn-newDev').click(function () {
            $('#newDevModal').empty();
            $('#newDevModal').append('<div class="modal-dialog">' +
                    '<div class="modal-content">' +
                    '<div class="modal-header">' +
                    '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
                    '<h2 class="modal-title" id="myModalLabel">New Device</h2>' +
                    '</div>' +
                    '<div class="modal-body" style="text-align: center;">' +
                    '<h4>Name:</h4>'+
                    '<input type="text" id="newDevModal-name">'+
                    '<h4>Type:</h4>'+
                    '<input type="text" id="newDevModal-type">'+
                    '<br><br>'+
                    '<button class="btn btn-primary" id="newDevModal-create">Create</button>'+
                    '</div>' +
                    '<div class="modal-footer">' +
                    '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>');
            $('#newDevModal').modal('show');
            $('#newDevModal-create').click(function(){
                swarmApi.createDevice($('#newDevModal-type').val(),$('#newDevModal-name').val(),function(){window.location.reload();},function(){window.location.reload();});
            });
        });
    });
}