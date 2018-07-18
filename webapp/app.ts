import * as angular from 'angular';

import '@fortawesome/fontawesome-free/css/fontawesome.css';
import '@fortawesome/fontawesome-free/css/solid.css';
import 'uikit';

import constants from './app.constants';
import components from './components/app.components';
import services from './services/app.services';
import routes, { setRoutes } from './routes/app.routes';
import resources from './resources/app.resources';
import filters from './app.filters';
import { Person } from './models/Person';
import { Env, Environment } from './app.types';

import './app.scss';

angular
    .module('momusApp', [
        constants.name,
        filters.name,
        services.name,
        resources.name,
        components.name,
        routes.name,
    ]);
/*
 Since the pages visible on the app is dependent on the environment and the logged in user,
 we fetch this and set the active routes before bootstrapping the application
 */
function initApp() {
    loadInitData().then((env) => {
        angular.module(constants.name)
            .constant('loggedInUser', env.loggedInUser)
            .constant('env', env.env);
        setRoutes(env);
        bootstrap();
    });
}

function bootstrap() {
    angular.element(window.document).ready(() => angular.bootstrap(window.document, ['momusApp'], {strictDi: true}));
}

async function loadInitData(): Promise<Environment> {
    const injector = angular.injector(['ng']);
    const $http = injector.get('$http');
    const loggedInUser = (await $http.get<Person>('/api/person/me')).data;
    const env = (await $http.get<Env>('/api/env/all')).data;
    return new Promise<Environment>((resolve) => resolve({loggedInUser, env}));
}

initApp();
