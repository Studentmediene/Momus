import * as angular from 'angular';

import articleDetails from './components/articleDetails/articleDetails.component';
import articleSearch from './components/articleSearch/articleSearch.component';
import { Environment } from '../../app.types';
import { StateProvider, $InjectorLike, StateParams } from '@uirouter/angularjs';
import { ArticleResource } from '../../resources/article.resource';
import { PersonResource } from '../../resources/person.resource';
import { Article } from '../../models/Article';

const routeModule = angular
    .module('momusApp.routes.article', [
        articleDetails.name,
        articleSearch.name,
    ])
    .config(($stateProvider: StateProvider) => {
        $stateProvider
            .state('article', {
                parent: 'root',
                redirectTo: 'article.search',
                data: {
                    nav: {
                        name: 'Artikler',
                        icon: 'fas fa-pencil-alt',
                    },
                },
            })
            .state('article.search', {
                url: '/artikler',
                component: 'articleSearch',
                data: {
                    title: 'Artikkelsøk',
                    nav: false,
                },
            })
            .state('article.details', {
                url: '/artikler/:id',
                component: 'articleDetails',
                data: {
                    title: (injector: $InjectorLike) => injector.get('article').name,
                    nav: false,
                },
                resolve: {
                    article: ($stateParams: StateParams, articleResource: ArticleResource) =>
                        articleResource.get({ id: $stateParams.id }).$promise,
                    articleContent: (article: Article, articleResource: ArticleResource) =>
                        articleResource.content(article.id).then((data) => data.data),
                    sections: (articleResource: ArticleResource) => articleResource.sections().$promise,
                    types: (articleResource: ArticleResource) => articleResource.types().$promise,
                    reviews: (articleResource: ArticleResource) => articleResource.reviewStatuses().$promise,
                    statuses: (articleResource: ArticleResource) => articleResource.statuses().$promise,
                    persons: (personResource: PersonResource, article: Article) =>
                        personResource.query({articleIds: [article.id]}).$promise,
                },
            });
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
