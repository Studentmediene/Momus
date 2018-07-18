import * as angular from 'angular';

import InfoCtrl from './info.ctrl';
import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';

const routeModule = angular
    .module('momusApp.routes.info', [])
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
    })
    .component('infoPage', {
        controller: InfoCtrl,
        controllerAs: 'vm',
        template: require('./info.html'),
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
