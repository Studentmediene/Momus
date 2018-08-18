import * as angular from 'angular';

import { Environment } from '../../app.types';
import PublicationDispositionCtrl from './publicationDisposition/publicationDisposition.ctrl';
import { StateProvider } from '@uirouter/angularjs';
import { PublicationResource } from 'resources/publication.resource';

import publicationOverview from './components/publicationOverview/publicationOverview.component';
import publicationDisposition from './components/publicationDisposition/publicationDisposition.component';

const routeModule = angular
    .module('momusApp.routes.publication', [
        publicationOverview.name,
        publicationDisposition.name,
    ])
    .config(($stateProvider: StateProvider) => {
        $stateProvider
            .state('publication', {
                parent: 'root',
                redirectTo: 'publication.overview',
            })
            .state('publication.overview', {
                url: '/utgaver',
                component: 'publicationOverview',
                data: {
                    nav: {
                        name: 'Utgaver',
                        icon: 'fas fa-list-ul',
                    },
                },
                resolve: {
                    publications: (publicationResource: PublicationResource) =>
                        publicationResource.query().$promise,
                },
            })
            .state('publication.disposition', {
                url: '/utgaver/:id/disposisjon',
                component: 'publicationDisposition',
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
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
