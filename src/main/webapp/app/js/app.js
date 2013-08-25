'use strict';

// Define the modules
angular.module('momusApp.controllers', []);
angular.module('momusApp.filters', []);
angular.module('momusApp.services', []);
angular.module('momusApp.directives', []);


// Declare app level module which depends on filters, and services
angular.module('momusApp', ['momusApp.controllers', 'momusApp.filters', 'momusApp.services', 'momusApp.directives']).
    config(['$routeProvider', function ($routeProvider) {
        // Admin interfaces
        $routeProvider.when('/admin/role', {templateUrl: 'partials/admin/role.html', controller: 'AdminRoleCtrl'});

        // Article interfaces
        $routeProvider.when('/article/:id', {templateUrl: 'partials/article/articleView.html', controller: 'ArticleCtrl'});


        $routeProvider.otherwise({redirectTo: '/view1'});
    }]);
