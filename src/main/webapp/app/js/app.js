/*
 * Copyright 2014 Studentmediene i Trondheim AS
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
        'ui.select',
        'ui.select2',
        'ui.bootstrap',
        'ngCookies',
        'ui.sortable'
    ]).
    config(['$routeProvider', function ($routeProvider) {
        // Admin interfaces
        $routeProvider


            .when('/artikler',
            {
                templateUrl: 'partials/search/searchView.html',
                controller: 'SearchCtrl',
                reloadOnSearch: false,
                title: "Artikkelsøk"
            }
        )
            .when('/artikler/ny',
            {
                templateUrl: 'partials/article/createArticle.html',
                controller: 'CreateArticleCtrl',
                title: "NyArtikkel"
            }
        )
            .when('/artikler/:id',
            {
                templateUrl: 'partials/article/articleView.html',
                controller: 'ArticleCtrl'
            }
        )

            .when('/artikler/revisjon/:id',
            {
                templateUrl: 'partials/article/articleRevisionView.html',
                controller: 'ArticleRevisionCtrl'
            }
        )

            .when('/utgaver',
            {
                templateUrl: 'partials/publication/publicationView.html',
                controller: 'PublicationCtrl',
                title: "Utgaver"
            }
        )

            //Disposition
            .when('/disposition/:id',
            {
                templateUrl: 'partials/disposition/dispositionView.html',
                controller: 'DispositionCtrl',
                title: "Disposisjon"
            }
        )
            .when('/disposition',
            {
                templateUrl:'partials/disposition/dispositionView.html',
                controller:'DispositionCtrl',
                title: "Disposisjon"
            }
        )

            // Sources
            .when('/kilder',
            {
                templateUrl: 'partials/source/search.html',
                controller: 'SourceSearchCtrl',
                reloadOnSearch: false,
                title: "Kilder"
            }
        )
            .when('/kilder/ny',
            {
                templateUrl: 'partials/source/edit.html',
                controller: 'SourceEditCtrl',
                title: "Ny kilde"
            }
        )
            .when('/kilder/tags',
            {
                templateUrl: 'partials/source/tags.html',
                controller: 'SourceTagsCtrl',
                title: "Rediger kildetags"
            }
        )
            .when('/kilder/:id',
            {
                templateUrl: 'partials/source/edit.html',
                controller: 'SourceEditCtrl'
            }
        )
            .when('/info',
            {
                templateUrl: 'partials/info/infoView.html',
                controller: 'InfoCtrl',
                title: 'Info'
            }
        )
            /*.when('/', {
                templateUrl: 'partials/front/frontPageView.html',
                controller: 'FrontPageCtrl'
            }
        )*/

            .otherwise({redirectTo: '/disposition'});

    }]).
    config(['$httpProvider', function ($httpProvider) {
        $httpProvider.interceptors.push('HttpInterceptor');
    }]).

    run(['$location', '$rootScope', 'TitleChanger', function ($location, $rootScope, TitleChanger) {
        // Whenever there is a route change, we try to update the url with the title set in the rootprovider above
        // if there is no title, we clear it
        $rootScope.$on('$routeChangeSuccess', function (event, current, previous) {
            if (current.$$route && current.$$route.title) {
                TitleChanger.setTitle(current.$$route.title);
            } else {
                TitleChanger.setTitle("");
            }
        });
    }]);

