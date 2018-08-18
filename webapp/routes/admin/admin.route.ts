import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';

import adminPage from 'routes/admin/admin.component';

const routeModule = angular
    .module('momusApp.routes.admin', [
        adminPage.name,
    ])
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
    });

export default {
    shouldLoadRoute: (env: Environment) => env.loggedInUser.roles.includes('ROLE_ADMIN'),
    module: routeModule,
};
