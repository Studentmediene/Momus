import * as angular from 'angular';

import { Environment } from '../../app.types';
import PublicationDispositionCtrl from './publicationDisposition/publicationDisposition.ctrl';
import PublicationOverviewCtrl from './publicationOverview/publicationOverview.ctrl';
import { StateProvider } from '@uirouter/angularjs';

const routeModule = angular
    .module('momusApp.routes.publication', [])
    .config(($stateProvider: StateProvider) => {
        $stateProvider
            .state('publication', {
                parent: 'root',
                redirectTo: 'publication.overview',
            })
            .state('publication.overview', {
                url: '/utgaver',
                component: 'publicationOverviewPage',
                data: {
                    nav: {
                        name: 'Utgaver',
                        icon: 'fas fa-list-ul',
                    },
                },
            })
            .state('publication.disposition', {
                url: '/utgaver/:id/disposisjon',
                component: 'publicationDispositionPage',
                params: {
                    id: {value: 'aktiv'},
                },
                resolve: {
                    persons: ($http: angular.IHttpService) => $http.get('api/person'),
                },
                data: {
                    nav: {
                        name: 'Disposisjon',
                        icon: 'fas fa-newspaper',
                    },
                },
            });
    })
    .component('publicationOverviewPage', {
        controller: PublicationOverviewCtrl,
        controllerAs: 'vm',
        template: require('./publicationOverview/publicationOverview.html'),
    })
    .component('publicationDispositionPage', {
        bindings: {
            persons: '<',
        },
        controller: PublicationDispositionCtrl,
        controllerAs: 'vm',
        template: require('./publicationDisposition/publicationDisposition.html'),
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
