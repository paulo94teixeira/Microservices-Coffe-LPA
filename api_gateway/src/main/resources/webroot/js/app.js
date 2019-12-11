'use strict';

var vertxApp = angular.module('CrudApp', [
    'ngRoute'
]);

/**
 * Config routes
 */
vertxApp.config(['$routeProvider', function ($routeProvider) {
        $routeProvider.
        when('/', {
            templateUrl: '/assets/view/initial_page.html'
        }).when('/main', {
            templateUrl: '/assets/view/main.html',
            controller: getAllMainList
        }).when('/account/:id', {
            templateUrl: '/assets/view/account.html'
        }).when('/registo', {
            templateUrl: '/assets/view/registo.html'
        }).when('/login', {
            templateUrl: '/assets/view/login.html'
        }).when('/404', {
            templateUrl: '/assets/view/404.html'
        }).otherwise({
            redirectTo: '/404'
        });
    }]);


function getAllMainList($scope, $http) {
     $scope.album_music = [];
     $scope.products = [];
     $scope.albuns = [];

     $http({
        method: 'GET',
        url: '/api/getSessionUser'
    }).success(data => {
        if (data.status == "200") {
            fetchPageOnSucessLogin();
        } else {
            window.location.href = '#/';
            window.location.reload();
        }
    }).error(() => {
    });

    var fetchPageOnSucessLogin = function () {
        $scope.album_music = [];
        $scope.albuns = [];
        $scope.products = [];
        $scope.albuns_backup = [];

        $http.get('/api/getProducts').success(function (data) {
            $scope.products = data;
        });

        $http.get('/api/getAlbuns').success(function (data) {
            $scope.albuns = data;
            $scope.albuns_backup = data;
        });

        $scope.get_album_musics = function (album) {
            $http({
                method: 'GET',
                url: '/api/getAlbum/' + album.id
            }).success(data => {
                if (data.length > 0) {
                    $scope.album_music = data;
                    $scope.index_of_music = 0;

                    handler_music_player("start");
                }else{
                    $scope.album_music = data;
                    handler_music_player("no_sound");
                }
            }).error((data, status, headers, config) => {
            });
        };

        $scope.get_all_products = function (product_id) {
            var albuns_temp = $scope.albuns_backup;
            $scope.albuns = [];
 
            $.each(albuns_temp, function (key, val) {
                if( val.product == product_id){
                    $scope.albuns.push(val);
                }
            });
        };

        $scope.get_all_albuns = function () {
            var albuns_temp = $scope.albuns_backup;
            $scope.albuns = [];
            $.each(albuns_temp, function (key, val) {
                $scope.albuns.push(val);
            });
        };

        $scope.procurar_all = function (word_search) {
            var albuns_temp = $scope.albuns_backup;
            $scope.albuns = [];
            $.each(albuns_temp, function (key, val) {
                if( val.title.toLowerCase().includes(word_search.toLowerCase())){
                    $scope.albuns.push(val);
                }
            });
        };

        $scope.handler_musics = function (handler_music) {
            handler_music_player(handler_music);

        };

        var handler_music_player = function (handler) {
            var musics = $scope.album_music;
            var index = $scope.index_of_music;
            if (musics.length > 0) {
                if (handler == "start") {
                    index = 0;
                } else if (handler == "previous") {
                    if (index > 0) {
                        index = index - 1;
                    } else {
                        index = musics.length - 1;
                    }
                } else if (handler == "next") {
                    if (index + 1 < musics.length) {
                        index = index + 1;
                    } else {
                        index = 0;
                    }
                }else if (handler == "no_sound"){
                    $scope.album_music = [];
                    $scope.index_of_music = 0;
                    document.getElementById('mysong').src = "";
                    $('#img_music').attr('src', "assets/img/default.jpg");
                    $('#track-desc').html('There are no tracks loaded in the player.');
                }
                // Show the artist and title.
                $scope.index_of_music = index;
                document.getElementById('mysong').src = musics[index].source;
                $('#track-desc').html('<b>' + musics[index].id + '  ' + musics[index].title + '</b>');
                $('#img_music').attr('src', "assets/" + musics[index].image);
            } else {
                $scope.album_music = [];
                $scope.index_of_music = 0;
                document.getElementById('mysong').src = "";
                $('#img_music').attr('src', "assets/img/default.jpg");
                $('#track-desc').html('There are no tracks loaded in the player.');
            }
        };
    };
}
//guarda os dados de user para utilizar em todos os controllers
vertxApp.service('SessionService', function ($window) {
    var service = this;
    var sessionStorage = $window.sessionStorage;

    service.get = function (key) {
        return sessionStorage.getItem(key);
    };

    service.set = function (key, value) {
        sessionStorage.setItem(key, value);
    };

    service.unset = function (key) {
        sessionStorage.removeItem(key);
    };
});


vertxApp.controller('LoginCtrl', ['$scope', '$http', '$rootScope', '$location', 'SessionService',
    function ($scope, $http, $rootScope, $location, SessionService) {

        validateUserHaveSession($http);

        $scope.efetuar_login = function (user) {

            $http.post('/api/login', user).success(function (data) {
                $scope.userData = data;

                if (data.status == "200") {
                    window.location.href = '#/main';
                    window.location.reload();
                } else {
                    handlerMensagensToUser(data, "/main");
                }

            });
        };
    }]);


vertxApp.controller('RegistoCtrl', ['$scope', '$http', '$location', function ($scope, $http, $location) {
        $scope.master = {};
        $scope.activePath = null;
        validateUserHaveSession($http);
        $scope.add_new_registo = function (user) {

            $http.post('/api/registo', user).success(function (data) {
                handlerMensagensToUser(data, "");
                $scope.reset();
            });

            $scope.reset = function () {
                $scope.user = angular.copy($scope.master);
            };

        };
    }]);



vertxApp.controller('AccountCtrl', ['$scope', '$http', '$rootScope', '$location', '$routeParams',
    function ($scope, $http, $rootScope, $location, $routeParams) {
        
        var id = $routeParams.id;
        $scope.activePath = null;
        $scope.user = {};

        $scope.update = function (user) {
            $http.put('/api/getUserData/' + id, user).success(function (data) {
                $scope.user = data;
                $scope.activePath = $location.path('/main');
            });
        };

        var fetchUser = function () {
            $http({
                method: 'GET',
                url: '/api/getUserData/' + id
            }).success(data => {
                $scope.user = data;
            }).error((data, status, headers, config) => {
            });
        };

        var getPacotesUser = function () {
            $http({
                method: 'GET',
                url: '/api/getPacotes'
            }).success(data => {
                $.each(data, function (key, val) {
                    var a = val.tipo_pacote;
                    $("<tr>" +
                            "<td>" +
                            "<button onclick='subscreverPacote("  +JSON.stringify(a)+ ")' type='button' class='btn btn-primary btn-lg' id='selectedStreamToPlay' " +
                            "><span class='fas fa-money-bill'></span>" +
                            "</button>" +
                            "&nbsp;" +
                            "</td>" +
                            "<td>" + val.tipo_pacote + "</td>" +
                            "<td>" + val.preco +" "+ val.moeda + "</td>" +
                            "<td>" + val.descricao + "</td>" +
                            "</tr>"
                            ).appendTo("#contentCollection");
                });

            }).error((data, status, headers, config) => {
            });
        };

        $http({
            method: 'GET',
            url: '/api/getSessionUser'
        }).success(data => {
            if (data.status == "200") {
                fetchUser();
                getPacotesUser();
            } else {
                window.location.href = '#/';
                window.location.reload();
            }
        }).error(() => {
        });
        
        
        
    }]);

vertxApp.controller('HeaderCtrl', ['$scope', '$http', '$rootScope', '$location',
    function ($scope, $http, $rootScope, $location) {
        $scope.user = {}

        $http({
            method: 'GET',
            url: '/api/getSessionUser'
        }).success(data => {
            if (data.status == "200") {
                $scope.user = data;
            } else {
                $scope.user ={};
            }
        }).error(() => {
        });

        $scope.logout = function () {
            $http.post('/logout', {}).success(() => {
                $rootScope.authenticated = false;
                $scope.user = {};
                $rootScope.user = {};
                window.location.href = '#/';
                window.location.reload();
                $rootScope.$broadcast('logout', "update");
            }).error(() => {
                $scope.user = {};
                $rootScope.$broadcast('logout', "update");
            });
        };

    }]);

function handlerUserSeePages($http, permissao) {
    var data = getUserSessionData($http);
    console.log(data);
    if (typeof data == 'undefined') {
        window.location.href = '#/';
        window.location.reload();
    } else {
        return true;
    }
}

function validateUserHaveSession($http) {
    $http({
        method: 'GET',
        url: '/api/getSessionUser'
    }).success(data => {
        if (data.status == "200") {
            window.location.href = '#/main';
            window.location.reload();
        }
    }).error(() => {
    });
}

function getUserSessionData($http) {
    $http({
        method: 'GET',
        url: '/api/getSessionUser'
    }).success(data => {
        console.log("pedido user session");
        console.log(data);
        console.log(data.status);
        if (data.status == "200") {
            return data;
        } else {
            return {};
        }
    }).error(() => {
    });
}

function handlerMensagensToUser(data, path) {
    var x = document.getElementById("sucess_msm");
    var error = document.getElementById("error_msm");
    if (typeof data !== 'undefined') {
        if (data.status == "200") {
            x.style.display = "block";
            setInterval(function () {
                x.style.display = "none";
                if (path != "") {
                    window.location.href = "/assets/view" + path + ".html";
                }
            }, 3000);
        } else {
            error.style.display = "block";
            setInterval(function () {
                error.style.display = "none";
            }, 3000);
        }
    } else {
        error.style.display = "block";
        setInterval(function () {
            error.style.display = "none";
        }, 3000);
    }
}
    