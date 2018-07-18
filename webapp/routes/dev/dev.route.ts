import * as angular from 'angular';

import { Environment } from '../../app.types';

import DevCtrl from './dev.ctrl';
import { StateProvider } from '@uirouter/angularjs';

const routeModule = angular
    .module('momusApp.routes.dev', [])
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
        });
    })
    .component('devPage', {
        template: require('./dev.html'),
        controller: DevCtrl,
        controllerAs: 'vm',
    });

export default {
    shouldLoadRoute: (env: Environment) => env.env.devmode,
    module: routeModule,
};
