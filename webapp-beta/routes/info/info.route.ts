import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';

import infoPage from './info.component';

const routeModule = angular
    .module('momusApp.routes.info', [
        infoPage.name,
    ])
    .config(($stateProvider: StateProvider) => {
        $stateProvider
            .state('info', {
                parent: 'root',
                url: '/info',
                component: 'infoPage',
                data: {
                    nav: {
                        name: 'Info',
                        icon: 'fas fa-info-circle',
                    },
                },
            });
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
