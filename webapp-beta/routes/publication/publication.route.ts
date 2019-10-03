import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider, StateParams, StateService } from '@uirouter/angularjs';
import { PublicationResource } from 'resources/publication.resource';

import publicationOverview from './components/publicationOverview/publicationOverview.component';
import publicationDisposition from './components/publicationDisposition/publicationDisposition.component';
import { PageResource } from 'resources/page.resource';
import { Publication } from 'models/Publication';
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
                    title: 'Utgaver',
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
                    id: { value: 'aktiv' },
                },
                resolve: {
                    publication: (
                        activePublication: Publication,
                        publicationResource: PublicationResource,
                        $stateParams: StateParams,
                        $state: StateService,
                        $q: angular.IQService,
                    ) => {
                        if ($stateParams.id !== 'aktiv') {
                            return publicationResource.get({id: $stateParams.id}).$promise;
                        } else if (activePublication.id == null) {
                            $state.go('error', { message: 'Det finnes ingen aktiv utgave.' });
                            return $q.reject();
                        } else {
                            return activePublication;
                        }
                    },
                    pages: (publication: Publication, pageResource: PageResource) =>
                        pageResource.query({ publicationId: publication.id }),
                    pageOrder: (pageResource: PageResource, publication: Publication) =>
                        pageResource.pageOrder({ publicationId: publication.id }),
                    articles: (articleResource: ArticleResource, publication: Publication) =>
                        articleResource.query({publicationId: publication.id}),
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
