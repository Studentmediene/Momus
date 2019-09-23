import * as angular from 'angular';
import uirouter, { UrlRouterProvider, StateProvider } from '@uirouter/angularjs';
import { Transition, TransitionService, StateObject, StateParams } from '@uirouter/core';

import { Environment, RootScope } from '../app.types';
import navigation from './navigation/navigation.route';
import home from './home/home.route';
import article from './article/article.route';
import publication from './publication/publication.route';
import info from './info/info.route';
import admin from './admin/admin.route';
import dev from './dev/dev.route';
import TitleService from 'services/title.service';

function getNextStateTitle(transition: Transition) {
    const newState = transition.to();
    const { title } = newState.data;
    switch (typeof title) {
        case 'string': return title;
        case 'function': return title(transition.injector());
        default: return undefined;
    }
}

const routesModule = angular
    .module('momusApp.routes', [
        uirouter,
        navigation.name,
    ])
    .config(($urlRouterProvider: UrlRouterProvider, $locationProvider: angular.ILocationProvider) => {
        $urlRouterProvider.otherwise('/');
        $locationProvider.html5Mode(true);
    })
    .config(($stateProvider: StateProvider) => {
        $stateProvider.state('error', {
            parent: 'root',
            component: 'errorMessage',
            params: {
                message: 'Det har skjedd en feil',
            },
            resolve: {
                message: ($stateParams: StateParams) => $stateParams.message,
            },
            data: {
                title: 'Feil',
            },
        });
    })
    .run(($transitions: TransitionService, titleService: TitleService, $rootScope: RootScope, $document: angular.IDocumentService) => {
        $transitions.onSuccess({}, (transition) => {
            // Reset scroll position to top if we move to a different state.
            if (transition.to().name !== transition.from().name) {
                $document[0].scrollTop = $document[0].documentElement.scrollTop = 0;
            }
            // Set page title
            titleService.setTitle(getNextStateTitle(transition));
        });

        const initialStateMatcher = (state: StateObject) => state.name === '^';
        $transitions.onBefore({from: initialStateMatcher}, () => { $rootScope.initialLoad = true; });
        $transitions.onSuccess({from: initialStateMatcher}, () => { $rootScope.initialLoad = false; });

        $transitions.onBefore({from: (s) => !initialStateMatcher(s)}, () => { $rootScope.transitionLoad = true; });
        $transitions.onSuccess({from: (s) => !initialStateMatcher(s)}, () => { $rootScope.transitionLoad = false; });
    });

/*
The application only loads the route modules that should be visible
given the logged in user and environment
*/
export function setRoutes(env: Environment) {
    const routes = [
        home,
        article,
        publication,
        info,
        admin,
        dev,
    ];

    routes.filter((route) => route.shouldLoadRoute(env))
        .forEach((route) => routesModule.requires.push(route.module.name));
}

export default routesModule;
