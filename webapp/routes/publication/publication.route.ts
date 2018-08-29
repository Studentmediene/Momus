import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider, StateParams } from '@uirouter/angularjs';
import { PublicationResource } from 'resources/publication.resource';

import publicationOverview from './components/publicationOverview/publicationOverview.component';
import publicationDisposition from './components/publicationDisposition/publicationDisposition.component';
import { PageResource } from 'resources/page.resource';
import { Publication } from 'models/Publication';
import { AdvertResource } from 'resources/advert.resource';
import { ArticleResource } from 'resources/article.resource';
import { Session } from 'services/session.service';

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
                abstract: true,
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
                    publication: ($stateParams: StateParams, publicationResource: PublicationResource) =>
                        $stateParams.id === 'aktiv' ?
                            publicationResource.active().$promise :
                            publicationResource.get({id: $stateParams.id}).$promise,
                    pageOrder: (pageResource: PageResource, publication: Publication) =>
                        pageResource.pageOrder({ publicationId: publication.id }).$promise,
                    adverts: (advertResource: AdvertResource) =>
                        advertResource.query().$promise,
                    articleStatuses: (articleResource: ArticleResource) =>
                        articleResource.statuses().$promise,
                    reviewStatuses: (articleResource: ArticleResource) =>
                        articleResource.reviewStatuses().$promise,
                    layoutStatuses: (publicationResource: PublicationResource) =>
                        publicationResource.layoutStatuses().$promise,
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
