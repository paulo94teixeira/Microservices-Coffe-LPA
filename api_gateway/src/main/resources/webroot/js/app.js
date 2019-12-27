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
        }).otherwise({
            templateUrl: '/assets/view/login.html'
        });
    }]);


function getAllMainList($scope, $http) {
    $scope.products = [];
    $scope.albuns = [];

    $http({
        method: 'GET',
        url: '/api/getSessionUser'
    }).success(data => {
        if (data.status === "200") {
            fetchPageOnSucessLogin();
        } else {
            window.location.href = '#/';
            window.location.reload();
        }
    }).error(() => {
    });

    var fetchPageOnSucessLogin = function () {
        $scope.products = [];
        $scope.products_backup = [];
        $scope.tables = [];
        $scope.tables_backup = [];
        $scope.menus = [];
        $scope.menus_backup = [];

        $http.get('/api/getProducts').success(function (data) {
            $scope.products = data;
        });

        $http.get('/api/getTables').success(function (data) {
            $scope.tables = data;
            $scope.tables_backup = data;
        });

        $scope.get_all_tables = function (table) {
            $http({
                method: 'GET',
                url: '/api/getTables/' + table.id
            }).success(data => {
                if (data.length > 0) {
                    $scope.tables = data;
                    $scope.index_of_table = 0;
                    $tableTemp = table.id;
                } else {
                    $scope.tables = data;
                }
            }).error((data, status, headers, config) => {
            });
        };

        $http.get('/api/getMenus').success(function (data) {
            $scope.menus = data;
            $scope.menus_backup = data;
        });

        $scope.get_all_products = function (product_id) {
            var products_temp = $scope.products;
            $scope.products = [];
            $.each(products_temp, function (key, val) {
                $scope.products.push(val);
            });
        };

        $scope.add_product = function (product) {
            var table = prompt("Please enter the number of table", "1");
            $http.post('/api/updateTable').success(function (data) {
                handlerMensagensToUser(product, "");

            });
        };

        $scope.fillSideBar = function (table) {
            document.getElementById("texSide").innerHTML = table.products + "              Final Price:" + table.total;
        };
    };
}
//
//vertxApp.controller('add_product', ['$scope', '$http', '$location', function ($scope, $http, $location) {
//
//        var person = prompt("Please enter your name", "Harry Potter");
////        $scope.master = {};
////        $scope.activePath = null;
////        validateUserHaveSession($http);
////        $scope.add_product = function (id) {
////
////            $http.post('/api/getProducts', id).success(function (data) {
////                handlerMensagensToUser(data, "");
////                $scope.reset();
////            });
////
////            $scope.reset = function () {
////                $scope.user = angular.copy($scope.master);
////            };
////
////        };
//    }]);

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
                $scope.user = {};
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
    