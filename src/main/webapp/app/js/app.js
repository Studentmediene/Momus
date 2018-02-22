/*
 * Copyright 2016 Studentmediene i Trondheim AS
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
angular.module('momusApp.resources', []);


// Declare app level module which depends on filters, and services
angular.module('momusApp', [
        'momusApp.controllers',
        'momusApp.filters',
        'momusApp.services',
        'momusApp.directives',
        'momusApp.resources',
        'ngRoute',
        'ngResource',
        'ui.select',
        'ui.bootstrap',
        'ngCookies',
        'ui.sortable',
        'ui.sortable.multiselection',
        'chart.js',
        'ngStomp'
    ]).
    config(['$routeProvider', $routeProvider => {
        $routeProvider
            .when('/front',
            {
                    templateUrl: 'partials/front/frontPageView.html',
                    controller: 'FrontPageCtrl'
            })
            .when('/artikler',
            {
                templateUrl: 'partials/search/searchView.html',
                controller: 'SearchCtrl',
                reloadOnSearch: false,
                title: "Artikkelsøk"
            })
            .when('/artikler/:id',
            {
                templateUrl: 'partials/article/articleView.html',
                controller: 'ArticleCtrl',
                controllerAs: 'vm'
            })
            .when('/artikler/revisjon/:id',
            {
                templateUrl: 'partials/article/articleRevisionView.html',
                controller: 'ArticleRevisionCtrl'
            })
            .when('/reklamer/:id',{
              templateUrl: 'partials/advert/advertView.html',
              controller: 'AdvertCtrl',
              controllerAs: 'vm'
            })
            .when('/utgaver',
            {
                templateUrl: 'partials/publication/publicationView.html',
                controller: 'PublicationCtrl',
                title: 'Utgaver',
                controllerAs: 'vm'
            })
            //Disposition
            .when('/disposisjon/:id?',
            {
                templateUrl: 'partials/disposition/dispositionView.html',
                controller: 'DispositionCtrl',
                title: 'Disposisjon',
                controllerAs: 'vm'
            })
            .when('/info',
            {
                templateUrl: 'partials/info/infoView.html',
                controller: 'InfoCtrl',
                title: 'Info'
            })
            .when('/dev',
            {
                templateUrl: 'partials/dev/devView.html',
                controller: 'DevCtrl',
                title: 'Dev',
                controllerAs: 'vm'
            })
            .otherwise({redirectTo: 'front'});

    }]).
    config(['$locationProvider', $locationProvider => {
        $locationProvider.hashPrefix('');
    }]).
    config(['$httpProvider', $httpProvider => {
        $httpProvider.interceptors.push('HttpInterceptor');
        $httpProvider.defaults.withCredentials = true;
    }]).
    run(['$rootScope', 'TitleChanger', ($rootScope, TitleChanger) => {
        // Whenever there is a route change, we try to update the url with the title set in the rootprovider above
        // if there is no title, we clear it
        $rootScope.$on('$routeChangeSuccess', (event, current, previous) => {
            TitleChanger.setTitle(current.$$route && current.$$route.title || "");
        });
    }]);
