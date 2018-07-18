import * as angular from 'angular';

import AdminCtrl from './admin.ctrl';
import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';

const routeModule = angular
    .module('momusApp.routes.admin', [])
    .config(($stateProvider: StateProvider) => {
        $stateProvider.state('admin', {
            parent: 'root',
            url: '/admin',
            component: 'adminPage',
            data: {
                title: 'Admin',
                nav: {
                    name: 'Admin',
                    icon: 'fas fa-wrench',
                },
            },
        });
    })
    .component('adminPage', {
        bindings: {
            user: '<',
        },
        template: require('./admin.html'),
        controller: AdminCtrl,
        controllerAs: 'vm',
    });

export default {
    shouldLoadRoute: (env: Environment) => env.loggedInUser.roles.includes('ROLE_ADMIN'),
    module: routeModule,
};
