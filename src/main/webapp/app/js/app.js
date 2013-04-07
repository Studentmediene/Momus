'use strict';


// Declare app level module which depends on filters, and services
angular.module('momusApp', ['momusApp.filters', 'momusApp.services', 'momusApp.directives']).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/view1', {templateUrl: 'partials/partial1.html', controller: MyCtrl1});
        $routeProvider.when('/view2', {templateUrl: 'partials/partial2.html', controller: MyCtrl2});

        // Admin interfaces
        $routeProvider.when('/admin', {templateUrl: 'partials/admin/admin.html', controller: MyCtrl2});
        $routeProvider.when('/admin/person', {templateUrl: 'partials/admin/person.html', controller: AdminPersonCtrl});
        $routeProvider.otherwise({redirectTo: '/view1'});
    }]);
