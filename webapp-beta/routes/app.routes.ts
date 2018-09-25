import * as angular from 'angular';
import uirouter, { UrlRouterProvider } from '@uirouter/angularjs';
import { Transition, TransitionService, StateObject } from '@uirouter/core';

import { Environment, RootScope } from '../app.types';
import navigation from './navigation/navigation.route';
import home from './home/home.route';
import article from './article/article.route';
import publication from './publication/publication.route';
import info from './info/info.route';
import admin from './admin/admin.route';
import dev from './dev/dev.route';

function getNextStateTitle(transition: Transition) {
    const newState = transition.to();
    const { title } = newState.data;
    switch (typeof title) {
        case 'string': return title + ' - Momus';
        case 'function': return title(transition.injector()) + ' - Momus';
        default: return 'Momus';
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
    .run(($transitions: TransitionService, $rootScope: RootScope) => {
        $transitions.onSuccess({}, (transition) => { $rootScope.pageTitle = getNextStateTitle(transition); });

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
