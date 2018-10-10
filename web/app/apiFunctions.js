/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

if (baseUrl == undefined)
    var baseUrl = "/swarm/api/";

window.swarmApi = {};

window.swarmApi.login = function (username, password, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "json/user/login",
        data: {user: username, pass: password},
        cache: false,
        success: function (result) {
            if (typeof (success) === 'function') {
                success();
            }
        },
        error: function () {
            if (typeof (err) === 'function') {
                err();
            }
        }
    });
};

window.swarmApi.checkLoginInfo = function (callback, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "json/user/login",
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (result) {
            console.log(result);
            if (typeof (callback) === 'function') {
                callback(result);
            }
        },
        error: function (result) {
            if (typeof (err) === 'function') {
                err();
            }
        }
    });
};

window.swarmApi.logout = function () {
    $.ajax({
        type: "GET",
        url: baseUrl + "json/user/logout",
        success: function (result) {
            window.location = "/swarm/login.html"
        }
    });
};

window.swarmApi.getUserDevices = function (success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "json/deviceList",
        success: function (result) {
            if (typeof (success) === 'function') {
                success(result);
            }
        },
        err: function (result) {
            if (typeof (err) === 'function') {
                err();
            }
        }
    });
};

window.swarmApi.getDevice = function (id, key, type, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "json/device",
        data: {id: id, key: key, type: type},
        success: function (result) {
            if (typeof (success) === 'function') {
                success(result);
            }
        },
        err: function (result) {
            if (typeof (err) === 'function') {
                err();
            }
        }
    });
};

window.swarmApi.deleteDevice = function (id, key, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "json/device/manage",
        data: JSON.stringify({id: id, key: key, action: 'delete'}),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (result) {
            if (typeof (success) === 'function') {
                success(result);
            }
        },
        err: function (result) {
            if (typeof (err) === 'function') {
                err();
            }
        }
    });
};

window.swarmApi.createDevice = function (type, name, success, err) {
    swarmApi.checkLoginInfo(function (usr) {
        $.ajax({
            type: "POST",
            url: baseUrl + "json/device/manage",
            data: JSON.stringify({new_owner: usr.name, new_name: name, new_type: type, action: 'create'}),
            contentType: 'application/json; charset=utf-8',
            dataType: 'json',
            success: function (result) {
                if (typeof (success) === 'function') {
                    success(result);
                }
            },
            err: function (result) {
                if (typeof (err) === 'function') {
                    err();
                }
            }
        });
    }, function () {
        if (typeof (err) === 'function') {
            err();
        }
    })
}

window.swarmApi.updateDevice = function (id, key, new_key, new_type, new_name, new_lat, new_lng, success, err) {
    var data = {id: id, key: key, action: 'edit'};
    if (new_key != undefined) {
        data.new_key = new_key;
    }
    if (new_type != undefined) {
        data.new_type = new_type;
    }
    if (new_name != undefined) {
        data.new_name = new_name;
    }
    if (new_lat != undefined) {
        data.new_lat = new_lat;
    }
    if (new_lng != undefined) {
        data.new_lng = new_lng;
    }
    $.ajax({
        type: "POST",
        url: baseUrl + "json/device/manage",
        data: JSON.stringify(data),
        contentType: 'application/json; charset=utf-8',
        dataType: 'json',
        success: function (result) {
            if (typeof (success) === 'function') {
                success(result);
            }
        },
        err: function (result) {
            if (typeof (err) === 'function') {
                err();
            }
        }
    });
};

window.swarmApi.getDeviceHistory = function (id, key, since, before, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "json/device/history",
        data: {id: id, key: key, since: since, before: before},
        success: function (result) {
            if (typeof (success) === 'function') {
                success(result);
            }
        },
        err: function (result) {
            if (typeof (err) === 'function') {
                err();
            }
        }
    });
};
