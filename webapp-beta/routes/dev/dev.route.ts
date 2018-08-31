import * as angular from 'angular';
import { StateProvider } from '@uirouter/angularjs';

import { Environment } from '../../app.types';

import devPage from './dev.component';
import { PersonResource } from 'resources/person.resource';

const routeModule = angular
    .module('momusApp.routes.dev', [
        devPage.name,
    ])
    .config(($stateProvider: StateProvider) => {
        $stateProvider.state('dev', {
            parent: 'root',
            url: '/dev',
            component: 'devPage',
            data: {
                title: 'Utviklerinnstillinger',
                nav: {
                    name: 'Dev',
                    icon: 'fas fa-cogs',
                    class: 'navbar-dev',
                },
            },
            resolve: {
                persons: (personResource: PersonResource) => personResource.query().$promise,
            },
        });
    });

export default {
    shouldLoadRoute: (env: Environment) => env.env.devmode,
    module: routeModule,
};
