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

if ($('#devTable').length) {
    $(document).ready(function () {
        swarmApi.getUserNodes(function (data) {
            if (data.data) {
                var dataSet = [];
                for (var i = 0; i < data.data.length; i++) {
                    dataSet[i] = ['<a href="#' + data.data[i].id + '" onclick="swarmUi.showNode(' + data.data[i].id + ',\'' + data.data[i].key + '\')">' + data.data[i].id + '</a>', data.data[i].key, data.data[i].name, data.data[i].latitude, data.data[i].longitude];
                }
                $('#devTable').DataTable({
                    data: dataSet,
                    columns: [
                        {title: "Id"},
                        {title: "Key"},
                        {title: "Name"},
                        {title: "Latitude"},
                        {title: "Longitude"}
                    ]
                });
            }
        });
    });
}

window.swarmUi.showNode = function (deviceId, deviceKey) {
    if ($('#devModal').length) {
        $('#devModal').empty();
        $('#devModal').append('<div class="modal-dialog">' +
                '<div class="modal-content">' +
                '<div class="modal-header">' +
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
                '<h2 class="modal-title" id="myModalLabel">Node: ' + deviceId + '</h2>' +
                '</div>' +
                '<div class="modal-body" id="devModal-content">' +
                '</div>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                '</div>' +
                '</div>' +
                '</div>');
        $('#devModal').modal('show');
        swarmApi.getNode(deviceId, deviceKey, function (data) {
            if (!data.data){
                alert('error');
                return;
            }
            $('#devModal-content').empty();
            $('#devModal-content').append('<button style="float:right;" id="devModal-delete"><i class="fa fa-trash"></i></button>');
            $('#devModal-content').append('<button style="float:right;" id="devModal-save"><i class="fa fa-save"></i></button>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Id: </span>' + data.data.id + '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Key: </span><input id="devModal-key" style="border:none;" type="text" value="' + data.data.key + '"> </div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Name: </span><input id="devModal-name" style="border:none;" type="text" value="' + data.data.name + '"></div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Location: </span>(<input id="devModal-lat" style="border:none;" type="text" value="' + data.data.latitude + '">, <input id="devModal-lng" style="border:none;" type="text" value="' + data.data.longitude + '">)</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;"> Air Quality: </span>' + data.data.air + '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;"> Temperature: </span>' + data.data.temperature + '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;"> Humidity: </span>' + data.data.humidity + '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;"> UV Light: </span>' + data.data.uv + '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;"> IR Light: </span>' + data.data.ir+ '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;"> Visible Light: </span>' + data.data.visible + '</div>');
            $('#devModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.1em; font-weight: bold;"> Battery: </span>' + data.data.battery + '</div>');
            $('#devModal-content').append('<br><br><div><span style="display: inline-block; width: 30%; font-size: 1.5em;">History:</span><button style="float:right;" id="devModal-history-refresh"><i class="fa fa-refresh"></i></button></div>');
            $('#devModal-content').append('<div>Since: <input style="line-height: inherit; !important" type="date" id="devModal-history-since"> ' +
                    'Before: <input style="line-height: inherit; !important" type="date" id="devModal-history-before"></div>');
            $('#devModal-content').append('<div id="devModal-history-graph"></div>');
            $('#devModal-history-before').val(new Date().toISOString().substr(0, 10));
            $('#devModal-history-since').val(new Date(new Date().getTime() - (60 * 60 * 24 * 7 * 1000)).toISOString().substr(0, 10));
            $('#devModal-save').click(function () {
                swarmApi.updateNode(data.data.id, data.data.key, $('#devModal-key').val(), $('#devModal-name').val(), $('#devModal-lat').val(), $('#devModal-lng').val(), function (res) {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
            });

            $('#devModal-delete').click(function () {
                swarmApi.deleteNode(data.data.id, data.data.key, function (res) {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
                setTimeout(function () {
                    window.location.reload()
                }, 2000);
            });

            $('#devModal-history-refresh').click(function () {
                swarmApi.getNodeHistory(data.data.id, data.data.key, new Date($('#devModal-history-since').val()).getTime(), new Date($('#devModal-history-before').val()).getTime(), function (res) {
                    console.log(res);
                    $('#devModal-history-graph').empty();
                    if (res.data.length == 0) {
                        $('#devModal-history-graph').text('No Data');
                    } else {
                        $('#devModal-history-graph').css('width', '100%');
                        $('#devModal-history-graph').css('height', '500px');
                        var gData = [];
                        var addData = function(x) {
                            var t1 = res.data.filter(function (tm) {
                                return tm[x] != undefined;
                            });
                            var gEDat = {
                                y: t1.map(function (e) {
                                    return e[x]
                                }),
                                x: t1.map(function (e) {
                                    return new Date(e.time).toISOString()
                                }),
                                mode: 'lines+markers',
                                name: x
                            };
                            gData.push(gEDat);
                        }
                        addData('air');
                        addData('temperature');
                        addData('humidity');
                        addData('uv');
                        addData('ir');
                        addData('visible');
                        addData('battery');
                        console.log(gData);
                        Plotly.newPlot('devModal-history-graph', gData);
                    }
                })
            });
        });
    }
}


if ($('#devMap').length) {
    $(document).ready(function () {
        var map = L.map('devMap');
        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoianN0ZWciLCJhIjoiY2ptc2M3dmxyMDFmaTNrcndmYXd4djZtayJ9.TJmh8CuOCnJDeAEbmrw07A', {
            attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
            maxZoom: 22,
            id: 'mapbox.streets',
            accessToken: 'pk.eyJ1IjoianN0ZWciLCJhIjoiY2ptc2M3dmxyMDFmaTNrcndmYXd4djZtayJ9.TJmh8CuOCnJDeAEbmrw07A'
        }).addTo(map);

        swarmApi.getUserNodes(function (data) {
            if (data.data) {
                var aveLat = 0;
                var aveLng = 0;
                for (var i = 0; i < data.data.length; i++) {
                    var marker = L.marker([data.data[i].latitude, data.data[i].longitude]);
                    aveLat = aveLat + data.data[i].latitude;
                    aveLng = aveLng + data.data[i].longitude;
                    marker.bindPopup('<a href="#' + data.data[i].id + '" onclick="swarmUi.showNode(' + data.data[i].id + ',\'' + data.data[i].key + '\')">Node: ' + data.data[i].id + '</a>');
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

if ($('#newDevModal').length) {
    $(document).ready(function () {
        $('#btn-newDev').click(function () {
            $('#newDevModal').empty();
            $('#newDevModal').append('<div class="modal-dialog">' +
                    '<div class="modal-content">' +
                    '<div class="modal-header">' +
                    '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
                    '<h2 class="modal-title" id="myModalLabel">New Node</h2>' +
                    '</div>' +
                    '<div class="modal-body" style="text-align: center;">' +
                    '<h4>Name:</h4>' +
                    '<input type="text" id="newDevModal-name">' +
                    '<h4>Latitude:</h4>' +
                    '<input type="text" id="newDevModal-latitude">' +
                    '<h4>Longitude:</h4>' +
                    '<input type="text" id="newDevModal-longitude">' +
                    '<br><br>' +
                    '<button class="btn btn-primary" id="newDevModal-create">Create</button>' +
                    '</div>' +
                    '<div class="modal-footer">' +
                    '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>');
            $('#newDevModal').modal('show');
            $('#newDevModal-create').click(function () {
                swarmApi.createNode($('#newDevModal-name').val(), $('#newDevModal-latitude').val(), $('#newDevModal-longitude').val(), function () {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
            });
        });
    });
}









if ($('#roverTable').length) {
    $(document).ready(function () {
        swarmApi.getUserRovers(function (data) {
            if (data.data) {
                var dataSet = [];
                for (var i = 0; i < data.data.length; i++) {
                    dataSet[i] = ['<a href="#' + data.data[i].id + '" onclick="swarmUi.showRover(' + data.data[i].id + ',\'' + data.data[i].key + '\')">' + data.data[i].id + '</a>', data.data[i].key, data.data[i].name, data.data[i].latitude, data.data[i].longitude];
                }
                $('#roverTable').DataTable({
                    data: dataSet,
                    columns: [
                        {title: "Id"},
                        {title: "Key"},
                        {title: "Name"},
                        {title: "Latitude"},
                        {title: "Longitude"}
                    ]
                });
            }
        });
    });
}

window.swarmUi.showRover = function (roverId, roverKey) {
    if ($('#roverModal').length) {
        $('#roverModal').empty();
        $('#roverModal').append('<div class="modal-dialog">' +
                '<div class="modal-content">' +
                '<div class="modal-header">' +
                '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
                '<h2 class="modal-title" id="myModalLabel">Rover: ' + roverId + '</h2>' +
                '</div>' +
                '<div class="modal-body" id="roverModal-content">' +
                '</div>' +
                '<div class="modal-footer">' +
                '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                '</div>' +
                '</div>' +
                '</div>');
        $('#roverModal').modal('show');
        swarmApi.getRover(roverId, roverKey, function (data) {
            if (!data.data){
                alert('error');
                return;
            }
            $('#roverModal-content').empty();
            $('#roverModal-content').append('<button style="float:right;" id="roverModal-delete"><i class="fa fa-trash"></i></button>');
            $('#roverModal-content').append('<button style="float:right;" id="roverModal-save"><i class="fa fa-save"></i></button>');
            $('#roverModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Id: </span>' + data.data.id + '</div>');
            $('#roverModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Key: </span><input id="roverModal-key" style="border:none;" type="text" value="' + data.data.key + '"> </div>');
            $('#roverModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Name: </span><input id="roverModal-name" style="border:none;" type="text" value="' + data.data.name + '"></div>');
            $('#roverModal-content').append('<div><span style="display: inline-block; width: 30%; font-size: 1.5em;">Location: </span>(<input id="roverModal-lat" style="border:none;" type="text" value="' + data.data.latitude + '">, <input id="roverModal-lng" style="border:none;" type="text" value="' + data.data.longitude + '">)</div>');
            $('#roverModal-save').click(function () {
                swarmApi.updateRover(data.data.id, data.data.key, $('#roverModal-key').val(), $('#roverModal-name').val(), $('#roverModal-lat').val(), $('#roverModal-lng').val(), function (res) {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
            });

            $('#roverModal-delete').click(function () {
                swarmApi.deleteRover(data.data.id, data.data.key, function (res) {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
                setTimeout(function () {
                    window.location.reload()
                }, 2000);
            });

            
        });
    }
}


if ($('#roverMap').length) {
    $(document).ready(function () {
        var map = L.map('roverMap');
        L.tileLayer('https://api.tiles.mapbox.com/v4/{id}/{z}/{x}/{y}.png?access_token=pk.eyJ1IjoianN0ZWciLCJhIjoiY2ptc2M3dmxyMDFmaTNrcndmYXd4djZtayJ9.TJmh8CuOCnJDeAEbmrw07A', {
            attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
            maxZoom: 22,
            id: 'mapbox.streets',
            accessToken: 'pk.eyJ1IjoianN0ZWciLCJhIjoiY2ptc2M3dmxyMDFmaTNrcndmYXd4djZtayJ9.TJmh8CuOCnJDeAEbmrw07A'
        }).addTo(map);

        swarmApi.getUserRovers(function (data) {
            if (data.data) {
                var aveLat = 0;
                var aveLng = 0;
                for (var i = 0; i < data.data.length; i++) {
                    var marker = L.marker([data.data[i].latitude, data.data[i].longitude]);
                    aveLat = aveLat + data.data[i].latitude;
                    aveLng = aveLng + data.data[i].longitude;
                    marker.bindPopup('<a href="#' + data.data[i].id + '" onclick="swarmUi.showRover(' + data.data[i].id + ',\'' + data.data[i].key + '\')">Rover: ' + data.data[i].id + '</a>');
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

if ($('#newRoverModal').length) {
    $(document).ready(function () {
        $('#btn-newRover').click(function () {
            $('#newRoverModal').empty();
            $('#newRoverModal').append('<div class="modal-dialog">' +
                    '<div class="modal-content">' +
                    '<div class="modal-header">' +
                    '<button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>' +
                    '<h2 class="modal-title" id="myModalLabel">New Node</h2>' +
                    '</div>' +
                    '<div class="modal-body" style="text-align: center;">' +
                    '<h4>Name:</h4>' +
                    '<input type="text" id="newRoverModal-name">' +
                    '<h4>Latitude:</h4>' +
                    '<input type="text" id="newRoverModal-latitude">' +
                    '<h4>Longitude:</h4>' +
                    '<input type="text" id="newRoverModal-longitude">' +
                    '<br><br>' +
                    '<button class="btn btn-primary" id="newRoverModal-create">Create</button>' +
                    '</div>' +
                    '<div class="modal-footer">' +
                    '<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>' +
                    '</div>' +
                    '</div>' +
                    '</div>');
            $('#newRoverModal').modal('show');
            $('#newRoverModal-create').click(function () {
                swarmApi.createRover($('#newRoverModal-name').val(), $('#newRoverModal-latitude').val(), $('#newRoverModal-longitude').val(), function () {
                    window.location.reload();
                }, function () {
                    window.location.reload();
                });
            });
        });
    });
}