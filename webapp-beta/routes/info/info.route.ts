import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';

import infoPage from './info.component';
import { NewsItemResource } from 'resources/newsItem.resource';

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
                    title: 'Info',
                    nav: {
                        name: 'Info',
                        icon: 'fas fa-info-circle',
                    },
                },
                resolve: {
                    news: (newsItemResource: NewsItemResource) => newsItemResource.query().$promise,
                },
            });
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
