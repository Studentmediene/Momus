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
        'ui.router',
        'ui.select',
        'ui.bootstrap',
        'ngCookies',
        'ui.sortable',
        'ui.sortable.multiselection',
        'chart.js',
        'ngStomp'
    ]).
    config(['$stateProvider', ($stateProvider) => {
        $stateProvider
            .state('root', {
                resolve: {
                    loggedInPerson: Person => Person.me().$promise
                },
                templateUrl: 'partials/nav/navView.html',
                controller: 'NavbarCtrl',
                controllerAs: 'vm'
            })
            .state('front', {
                parent: 'root',
                url: '/',
                resolve: {
                    myArticles: (Article, loggedInPerson) =>
                        Article.search({}, {persons: [loggedInPerson.id], page_size: 9}).$promise,
                    recentArticles: (ViewArticleService, Article) => {
                        const recents = ViewArticleService.getRecentViews();
                        return recents.length > 0 ?
                            Article.multiple({ids: recents}).$promise :
                            [];
                    },
                    favouriteSectionArticles: (Article, loggedInPerson) => {
                        return loggedInPerson.favouritesection ?
                            Article.search({}, {section: loggedInPerson.favouritesection.id}).$promise :
                            [];
                    },
                    activePublication: Publication => Publication.active().$promise
                        .then(pub => pub)
                        .catch(() => null),
                    sections: Article => Article.sections().$promise,
                    statuses: Article => Article.statuses().$promise,
                    statusCounts: (activePublication, Article) => {
                        return activePublication ?
                            Article.statusCounts({publicationId: activePublication.id}).$promise :
                            [];
                    },
                    reviewStatuses: Article => Article.reviewStatuses().$promise,
                    reviewStatusCounts: (activePublication, Article) => {
                        return activePublication ?
                            Article.reviewStatusCounts({publicationId: activePublication.id}).$promise :
                            [];
                    },
                    layoutStatuses: Publication => Publication.layoutStatuses().$promise,
                    layoutStatusCounts: (activePublication, Page) => {
                        return activePublication ?
                            Page.layoutStatusCounts({pubid: activePublication.id}).$promise :
                            [];
                    }

                },
                templateUrl: 'partials/front/frontPageView.html',
                controller: 'FrontPageCtrl',
                controllerAs: 'vm'
            })
            .state('search', {
                parent: 'root',
                url: '/artikler?publication&section&status&persons&page_number&page_size&review&free',
                templateUrl: 'partials/search/searchView.html',
                controller: 'SearchCtrl',
                controllerAs: 'vm',
                reloadOnSearch: false,
                title: "Artikkelsøk"
            })
            .state('article', {
                parent: 'root',
                url: '/artikler/:id',
                templateUrl: 'partials/article/articleView.html',
                controller: 'ArticleCtrl',
                controllerAs: 'vm'
            })
            .state('articlerevision', {
                parent: 'root',
                url: '/artikler/:id/revisjon',
                templateUrl: 'partials/article/articleRevisionView.html',
                controller: 'ArticleRevisionCtrl',
                controllerAs: 'vm'
            })
            .state('disposition', {
                parent: 'root',
                url: '/disposisjon/:id',
                params: {
                    id: {squash: true, value: null}
                },
                templateUrl: 'partials/disposition/dispositionView.html',
                controller: 'DispositionCtrl',
                controllerAs: 'vm',
                title: 'Disposisjon'
            })
            .state('publications', {
                parent: 'root',
                url: '/utgaver',
                templateUrl: 'partials/publication/publicationView.html',
                controller: 'PublicationCtrl',
                controllerAs: 'vm',
                title: 'Utgaver'
            })
            .state('info', {
                parent: 'root',
                url: '/info',
                templateUrl: 'partials/info/infoView.html',
                controller: 'InfoCtrl',
                controllerAs: 'vm',
                title: 'Info'
            })
            .state('dev', {
                parent: 'root',
                url: '/dev',
                templateUrl: 'partials/dev/devView.html',
                controller: 'DevCtrl',
                controllerAs: 'vm',
                title: 'Utviklerinnstillinger'
            });
    }]).
    config(['$urlRouterProvider', '$locationProvider', ($urlRouterProvider, $locationProvider) => {
        $urlRouterProvider.otherwise('/');
        $locationProvider.hashPrefix('');
    }]).
    config(['$httpProvider', $httpProvider => {
        $httpProvider.interceptors.push('HttpInterceptor');
        $httpProvider.defaults.withCredentials = true;
    }]).
    constant('RESOURCE_ACTIONS', {
        save: {method: 'POST'},
        get:    {method: 'GET'},
        query: {method: 'GET', isArray: true},
        update: {method: 'PUT'},
        delete: {method: 'DELETE'}
    }).
    config(['$resourceProvider', 'RESOURCE_ACTIONS', ($resourceProvider, RESOURCE_ACTIONS) => {
        $resourceProvider.defaults.actions = RESOURCE_ACTIONS;
    }]).
    run(['$transitions', 'TitleChanger', ($transitions, TitleChanger) => {
        $transitions.onSuccess({}, transition => {
            TitleChanger.setTitle(transition.to().title || "")
        });
    }]);