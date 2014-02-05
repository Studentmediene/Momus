/*
 * Copyright 2013 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

// Define the modules
angular.module('momusApp.controllers', []);
angular.module('momusApp.filters', []);
angular.module('momusApp.services', []);
angular.module('momusApp.directives', []);


// Declare app level module which depends on filters, and services
angular.module('momusApp', [
        'momusApp.controllers',
        'momusApp.filters',
        'momusApp.services',
        'momusApp.directives',
        'ngRoute',
        'ui.select2'
    ]).
    config(['$routeProvider', function ($routeProvider) {
        // Admin interfaces
        $routeProvider.
            when('/admin/role',
            {
                templateUrl: 'partials/admin/role.html',
                controller: 'AdminRoleCtrl'
            }
        )
        // Article interfaces
        .when('/article',
            {
                redirectTo: '/article/1' // TODO: make this go to article search view?
            }
        )
        .when('/article/:id',
            {
                templateUrl: 'partials/article/articleView.html',
                controller: 'ArticleCtrl'
            }
        )

        // Search interfaces
        .when('/search',
            {
                templateUrl: 'partials/search/searchView.html',
                controller: 'SearchCtrl'
            }
        )

        // Publications (utgaver) interfaces
        .when('/publications',
            {
                templateUrl: 'partials/publication/publicationView.html',
                controller: 'PublicationCtrl'
            }
        )

        //Disposition
        .when('/disposition/:id',
            {
                templateUrl: 'partials/disposition/dispositionView.html',
                controller: 'DispositionCtrl'
            }
        )

        // Sources
        .when('/source',
            {
                templateUrl: 'partials/source/search.html',
                controller: 'SourceSearchCtrl',
                reloadOnSearch: false
            }
        )

        .otherwise({redirectTo: '/'});

    }]).
    config(['$httpProvider', function($httpProvider) {
        $httpProvider.interceptors.push('HttpInterceptor');
    }])

;
