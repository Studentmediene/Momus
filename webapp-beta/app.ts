import * as angular from 'angular';

import '@fortawesome/fontawesome-free/css/fontawesome.css';
import '@fortawesome/fontawesome-free/css/solid.css';
import 'uikit';

import filters from './app.filters';
import services from './services/app.services';
import resources from './resources/app.resources';
import directives from './app.directives';
import components from './components/app.components';
import routes, { setRoutes } from './routes/app.routes';

import { Person } from './models/Person';
import { Env, Environment } from './app.types';
import httpInterceptorFactory from './services/httpInterceptor.factory';

import './style/app.scss';

const app = angular
    .module('momusApp', [
        filters.name,
        services.name,
        resources.name,
        directives.name,
        components.name,
        routes.name,
    ])
    .config(($httpProvider: angular.IHttpProvider) => {
        $httpProvider.interceptors.push(httpInterceptorFactory);
    });
/*
 Since the pages visible on the app is dependent on the environment and the logged in user,
 we fetch this and set the active routes before bootstrapping the application
 */
function initApp() {
    loadInitData().then((env) => {
        app
            .constant('loggedInUser', env.loggedInUser)
            .constant('env', env.env);
        setRoutes(env);
        bootstrap();
    });
}

function bootstrap() {
    angular.element(window.document).ready(() => angular.bootstrap(window.document, [app.name], {strictDi: true}));
}

async function loadInitData(): Promise<Environment> {
    const injector = angular.injector(['ng']);
    const $http = injector.get('$http');
    const loggedInUser = (await $http.get<Person>('/api/person/me')).data;
    const env = (await $http.get<Env>('/api/env/all')).data;
    return new Promise<Environment>((resolve) => resolve({loggedInUser, env}));
}

initApp();
