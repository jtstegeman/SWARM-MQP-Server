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
            console.log(result);
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
            console.log(result);
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

