import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider, StateParams } from '@uirouter/angularjs';
import { PublicationResource } from 'resources/publication.resource';

import publicationOverview from './components/publicationOverview/publicationOverview.component';
import publicationDisposition from './components/publicationDisposition/publicationDisposition.component';
import { PageResource } from 'resources/page.resource';
import { Publication, SimplePublication } from 'models/Publication';
import { AdvertResource } from 'resources/advert.resource';
import { ArticleResource } from 'resources/article.resource';

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
                        publicationResource.query(),
                },
            })
            .state('publication.disposition', {
                url: '/utgaver/:id/disposisjon',
                component: 'publicationDisposition',
                params: {
                    id: {value: 'aktiv'},
                },
                resolve: {
                    publicationId: ($stateParams: StateParams, activePublication: SimplePublication) =>
                        $stateParams.id === 'aktiv' ? activePublication.id : $stateParams.id,
                    publication: (publicationId: number, publicationResource: PublicationResource) =>
                        publicationResource.get({ id: publicationId }),
                    pageOrder: (pageResource: PageResource, publicationId: number) =>
                        pageResource.pageOrder({ publicationId }),
                    adverts: (advertResource: AdvertResource) =>
                        advertResource.query(),
                    articleStatuses: (articleResource: ArticleResource) =>
                        articleResource.statuses(),
                    reviewStatuses: (articleResource: ArticleResource) =>
                        articleResource.reviewStatuses(),
                    layoutStatuses: (publicationResource: PublicationResource) =>
                        publicationResource.layoutStatuses(),
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
