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

import 'jquery';
import 'jquery-ui';
import 'bootstrap';

import 'angular';
import 'angular-resource';
import 'angular-cookies';
import '@uirouter/angularjs';
import 'angular-ui-bootstrap';

import 'chartjs';
import 'angular-chart.js';

import 'ui-select';
import 'angular-ui-sortable';
import 'angular-ui-sortable-multiselection';

// Import css
import 'bootstrap/dist/css/bootstrap.css';
import '@fortawesome/fontawesome-free/css/fontawesome.css';
import '@fortawesome/fontawesome-free/css/solid.css';

import 'ui-select/dist/select.css'
import 'select2-bootstrap-theme/dist/select2-bootstrap.css';

import '../style/app.scss';

import resources from './resources';
import services from './services';
import directives from './directives';
import controllers from './controllers';

// Declare app level module
/* @ngInject */
angular.module('momusApp', [
        services.name,
        resources.name,
        directives.name,
        controllers.name,
        'ngResource',
        'ui.router',
        'ui.select',
        'ui.bootstrap',
        'ngCookies',
        'ui.sortable',
        'ui.sortable.multiselection',
        'chart.js'
    ]).
    run((CookieService, StaticValues, $window) => {
        const useBeta = CookieService.getUseBeta();
        StaticValues.environment().$promise.then(env => {
            if(!env.devmode && useBeta) {
                $window.location.href = "/beta";
            }
        });
    }).
    config(($stateProvider) => {
        $stateProvider
            .state('root', {
                resolve: {
                    loggedInPerson: Person => Person.me().$promise,
                    env: StaticValues => StaticValues.environment(),
                    session: (loggedInPerson, MessagingService) => MessagingService.createSession(loggedInPerson)
                },
                templateUrl: '/assets/partials/nav/navView.html',
                controller: 'NavbarCtrl',
                controllerAs: 'vm'
            })
            .state('front', {
                parent: 'root',
                url: '/',
                resolve: {
                    myArticles: (Article, loggedInPerson) =>
                        Article.search({}, {persons: [loggedInPerson.id], page_size: 9}).$promise,
                    recentArticles: (CookieService, Article) => {
                        const recents = CookieService.getRecentViews();
                        return recents.length > 0 ?
                            Article.multiple({ids: recents}).$promise :
                            [];
                    },
                    sectionArticles: (Article, loggedInPerson) => {
                        return loggedInPerson.section ?
                            Article.search(
                                {}, 
                                {
                                    section: loggedInPerson.section.id,
                                    page_number: 0,
                                    page_size: 9,
                                }).$promise :
                            [];
                    },
                    activePublication: Publication => Publication.active().$promise,
                    sections: Section => Section.query().$promise,
                    statusCounts: (activePublication, Article) => {
                        return activePublication.id != null ?
                            Article.statusCounts({publicationId: activePublication.id}).$promise :
                            [];
                    },
                    reviewStatusCounts: (activePublication, Article) => {
                        return activePublication.id != null ?
                            Article.reviewStatusCounts({publicationId: activePublication.id}).$promise :
                            [];
                    },
                    layoutStatusCounts: (activePublication, Page) => {
                        return activePublication.id != null ?
                            Page.layoutStatusCounts({pubid: activePublication.id}).$promise :
                            [];
                    },
                    news: (NewsItem) =>
                            NewsItem.query({}).$promise

                },
                templateUrl: '/assets/partials/front/frontPageView.html',
                controller: 'FrontPageCtrl',
                controllerAs: 'vm'
            })
            .state('search', {
                parent: 'root',
                url: '/artikler?defaultSearch&publication&section&status&review&free&persons&page_number&page_size&archived',
                params: {
                    defaultSearch: {type: 'bool'},
                    publication: {type: 'int'},
                    section: {type: 'int'},
                    status: {type: 'string'},
                    review: {type: 'string'},
                    free: {type: 'string'},
                    persons: {type: 'int', array: true},
                    page_number: {type: 'int', value: 0},
                    page_size: {type: 'int', value: 10},
                    archived: {type: 'bool'}
                },
                resolve: {
                    activePublication: Publication => Publication.active(),
                    publications: Publication => Publication.query().$promise,
                    persons: Person => Person.query().$promise,
                    sections: Section => Section.query().$promise,
                    searchParams: ($stateParams, activePublication) => {
                        const {['#']: _, ['defaultSearch']: defaultSearch, ...searchParams} = $stateParams;

                        if(defaultSearch) {
                            searchParams.publication = activePublication.id;
                        }
                        return searchParams;
                    },
                    results: (Article, searchParams) => Article.search({}, searchParams).$promise
                },
                templateUrl: '/assets/partials/search/searchView.html',
                controller: 'SearchCtrl',
                controllerAs: 'vm',
                reloadOnSearch: true,
                title: "Artikkelsøk"
            })
            .state('article', {
                parent: 'root',
                url: '/artikler/:id',
                templateUrl: '/assets/partials/article/articleView.html',
                controller: 'ArticleCtrl',
                controllerAs: 'vm'
            })
            .state('articlerevision', {
                parent: 'root',
                url: '/artikler/:id/revisjon',
                templateUrl: '/assets/partials/article/articleRevisionView.html',
                controller: 'ArticleRevisionCtrl',
                controllerAs: 'vm'
            })
            .state('disposition', {
                parent: 'root',
                url: '/disposisjon/:id',
                params: {
                    id: {squash: true, value: null}
                },
                resolve: {
                    publication: ($q, $state, $stateParams, Publication) => {
                        if ($stateParams.id == null) {
                            return Publication.active().$promise.then(publication => {
                                if (publication.id == null) {
                                    $state.go('front');
                                    return $q.reject();
                                }
                                return publication;
                            });
                        } else {
                            return Publication.get({id: $stateParams.id}).$promise;
                        }
                    },
                    pages: (publication, Page) => Page.query({publicationId: publication.id}),
                    pageOrder: (publication, Page) => Page.pageOrder({publicationId: publication.id}),
                    articles: (publication, Article) => Article.query({publicationId: publication.id}),
                    adverts: Advert => Advert.query(),
                },
                templateUrl: '/assets/partials/disposition/dispositionView.html',
                controller: 'DispositionCtrl',
                controllerAs: 'vm',
                title: 'Disposisjon'
            })
            .state('publications', {
                parent: 'root',
                url: '/utgaver',
                resolve: {
                    publications: Publication => Publication.query().$promise
                },
                templateUrl: '/assets/partials/publication/publicationView.html',
                controller: 'PublicationCtrl',
                controllerAs: 'vm',
                title: 'Utgaver'
            })
            .state('info', {
                parent: 'root',
                url: '/info',
                resolve: {
                    news: (NewsItem) =>
                        NewsItem.query({}).$promise
                },
                templateUrl: '/assets/partials/info/infoView.html',
                controller: 'InfoCtrl',
                controllerAs: 'vm',
                title: 'Info'
            })
            .state('admin',
            {
                parent: 'root',
                url: '/admin',
                resolve: {
                    news: NewsItem => NewsItem.query().$promise, 
                    articleTypes: ArticleType => ArticleType.query().$promise
                },
                templateUrl: '/assets/partials/admin/adminView.html',
                controller: 'AdminCtrl',
                title: 'Admin',
                controllerAs: 'vm',
                access: ["ROLE_ADMIN"]
            })
            .state('dev', {
                parent: 'root',
                url: '/dev',
                templateUrl: '/assets/partials/dev/devView.html',
                controller: 'DevCtrl',
                controllerAs: 'vm',
                title: 'Utviklerinnstillinger'
            });
    }).
    config(($urlRouterProvider, $locationProvider) => {
        $urlRouterProvider.otherwise('/');
        $locationProvider.hashPrefix('');
        $locationProvider.html5Mode(true);
    }).
    config($httpProvider => {
        $httpProvider.interceptors.push('HttpInterceptor');
        $httpProvider.defaults.withCredentials = true;
    }).
    constant('RESOURCE_ACTIONS', {
        save: {method: 'POST'},
        get:    {method: 'GET'},
        query: {method: 'GET', isArray: true},
        update: {method: 'PUT'},
        delete: {method: 'DELETE'}
    }).
    config(($resourceProvider, RESOURCE_ACTIONS) => {
        $resourceProvider.defaults.actions = RESOURCE_ACTIONS;
    }).
    run(($transitions, TitleChanger, $state, $rootScope, hasPermission, MessageModal) => {
        $transitions.onStart({}, transition => {
            if(transition.from().name === '')
                $rootScope.initialLoad = true;
            else
                $rootScope.transitionLoad = true;
        });

        $transitions.onEnter({}, transition => {
            if(!hasPermission(transition.to(), transition.injector().get('loggedInPerson'))) {
                MessageModal.error("Du har ikke tilgang til denne siden!", false);

                // Redirects to front page or previous state
                if(transition.from().name === "")
                    return transition.router.stateService.target('front');
                else
                    return false;
            }
        });

        $transitions.onSuccess({}, transition => {
            TitleChanger.setTitle(transition.to().title || "");

            if(transition.from().name === '')
                $rootScope.initialLoad = false;
            else
                $rootScope.transitionLoad = false;
        });

        $transitions.onStart({to: 'search'}, transition => {
            const {['#']: _, ...params} = transition.params();
            // Do default search if we come from another state and no parameters are specified or their default value
            if(
                transition.from().name !== 'search' &&
                !Object.keys(params).some(p => params[p] !== transition.to().params[p].value)
            ){
                return transition.router.stateService.target(transition.to(), {defaultSearch: true});
            }

        })
    });
