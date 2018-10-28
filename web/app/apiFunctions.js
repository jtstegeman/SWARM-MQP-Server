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

window.swarmApi.getUserNodes = function (success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "node/user",
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

window.swarmApi.getNode = function (id, key, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "node",
        data: {id: id, key: key},
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

window.swarmApi.deleteNode = function (id, key, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "node/delete",
        data: {id: id, key: key},
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

window.swarmApi.createNode = function (name, lat, lng, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "node/create",
        data: {latitude: lat, longitude: lng, name: name},
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

window.swarmApi.updateNode = function (id, key, new_key, new_name, new_lat, new_lng, success, err) {
    var data = {id: id, key: key};
    if (new_key != undefined) {
        data.nKey = new_key;
    }
    if (new_name != undefined) {
        data.name = new_name;
    }
    if (new_lat != undefined) {
        data.latitude = new_lat;
    }
    if (new_lng != undefined) {
        data.longitude = new_lng;
    }
    $.ajax({
        type: "POST",
        url: baseUrl + "node",
        data: data,
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

window.swarmApi.getNodeHistory = function (id, key, since, before, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "node/history",
        data: {id: id, key: key, after: since, before: before},
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




window.swarmApi.getUserRovers = function (success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "rover/user",
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

window.swarmApi.getRover = function (id, key, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "rover",
        data: {id: id, key: key},
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

window.swarmApi.deleteRover = function (id, key, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "rover/delete",
        data: {id: id, key: key},
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

window.swarmApi.createRover = function (name, lat, lng, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "rover/create",
        data: {latitude: lat, longitude: lng, name: name},
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

window.swarmApi.updateRover = function (id, key, new_key, new_name, new_lat, new_lng, success, err) {
    var data = {id: id, key: key};
    if (new_key != undefined) {
        data.nKey = new_key;
    }
    if (new_name != undefined) {
        data.name = new_name;
    }
    if (new_lat != undefined) {
        data.latitude = new_lat;
    }
    if (new_lng != undefined) {
        data.longitude = new_lng;
    }
    $.ajax({
        type: "POST",
        url: baseUrl + "rover",
        data: data,
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

window.swarmApi.getRoverHistory = function (id, key, since, before, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "rover/history",
        data: {id: id, key: key, after: since, before: before},
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


window.swarmApi.getRoverCmd = function (id, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "rover/cmd",
        data: {id: id},
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

window.swarmApi.deleteRoverCmd = function (id, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "rover/cmd/delete",
        data: {id: id},
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

window.swarmApi.createRoverCmd = function (roverId, lat, lng, success, err) {
    $.ajax({
        type: "POST",
        url: baseUrl + "rover/cmd/create",
        data: {latitude: lat, longitude: lng, roverId: roverId},
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

window.swarmApi.updateRoverCmd = function (id, new_lat, new_lng, new_cmd, success, err) {
    var data = {id: id};
    if (new_cmd != undefined) {
        data.cmd = new_cmd;
    }
    if (new_lat != undefined) {
        data.latitude = new_lat;
    }
    if (new_lng != undefined) {
        data.longitude = new_lng;
    }
    $.ajax({
        type: "POST",
        url: baseUrl + "rover/cmd",
        data: data,
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

window.swarmApi.getRoverCmds = function (roverId, success, err) {
    $.ajax({
        type: "GET",
        url: baseUrl + "rover/cmd/list",
        data: {roverId: roverId},
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