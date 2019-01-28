import * as angular from 'angular';

import articleDetails from './components/articleDetails/articleDetails.component';
import articleSearch from './components/articleSearch/articleSearch.component';
import articleRevisions from './components/articleRevisions/articleRevisions.component';
import { Environment } from '../../app.types';
import {
    StateProvider,
    $InjectorLike,
    StateParams,
    TransitionService,
    Transition,
} from '@uirouter/angularjs';
import { ArticleResource } from '../../resources/article.resource';
import { PersonResource } from '../../resources/person.resource';
import { Article } from '../../models/Article';
import { createArticleSearchParams } from './components/articleSearch/utils';
import { PublicationResource } from 'resources/publication.resource';
import CookieService from 'services/cookies.service';
import { ArticleSearchParams } from 'models/ArticleSearchParams';

const routeModule = angular
    .module('momusApp.routes.article', [
        articleDetails.name,
        articleSearch.name,
        articleRevisions.name,
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
                url: `/artikler?
                    defaultSearch&
                    publication&
                    section&
                    status&
                    review&
                    free&
                    persons&
                    page_number&
                    page_size&
                    archived`,
                params: {
                    defaultSearch: {type: 'bool'},
                    publication: {type: 'int'},
                    section: {type: 'int'},
                    status: {type: 'int'},
                    review: {type: 'int'},
                    free: {type: 'string'},
                    persons: {type: 'int', array: true},
                    page_number: {type: 'int', value: 0},
                    page_size: {type: 'int', value: 10},
                    archived: {type: 'bool'},
                },
                component: 'articleSearch',
                data: {
                    title: 'Artikkelsøk',
                    nav: false,
                },
                resolve: {
                    publications: (publicationResource: PublicationResource) =>
                        publicationResource.query(),
                    persons: (personResource: PersonResource) =>
                        personResource.query(),
                    sections: (articleResource: ArticleResource) =>
                        articleResource.sections().$promise,
                    statuses: (articleResource: ArticleResource) =>
                        articleResource.statuses().$promise,
                    reviews: (articleResource: ArticleResource) =>
                        articleResource.reviewStatuses().$promise,
                    types: (articleResource: ArticleResource) =>
                        articleResource.types().$promise,
                    searchParams: createArticleSearchParams,
                    results: (articleResource: ArticleResource, searchParams: ArticleSearchParams) =>
                        articleResource.search({}, searchParams.stringify()),
                },
                reloadOnSearch: true,
            })
            .state('article.single', {
                url: '/artikler/:id',
                redirectTo: 'article.single.details',
                data: {
                    title: (injector: $InjectorLike) => injector.get('article').name,
                    nav: false,
                },
                resolve: {
                    articleId: ($stateParams: StateParams) => $stateParams.id,
                    article: (articleId: number, articleResource: ArticleResource) =>
                        articleResource.get({ id: articleId }),
                    articleContent: (articleId: number, articleResource: ArticleResource) =>
                        articleResource.content({id: articleId}).then((data) => data.data),
                },
            })
            .state('article.single.details', {
                url: '/detaljer',
                component: 'articleDetails',
                resolve: {
                    sections: (articleResource: ArticleResource) => articleResource.sections(),
                    types: (articleResource: ArticleResource) => articleResource.types(),
                    reviews: (articleResource: ArticleResource) => articleResource.reviewStatuses(),
                    statuses: (articleResource: ArticleResource) => articleResource.statuses(),
                    persons: (personResource: PersonResource, articleId: number) =>
                        personResource.query({articleIds: [articleId]}),
                    publications: (publicationResource: PublicationResource) =>
                        publicationResource.query(),
                },
                onEnter: (cookieService: CookieService, articleId: number) => {
                    cookieService.addToRecentArticles(articleId);
                },
            })
            .state('article.single.revisions', {
                url: '/revisjoner',
                component: 'articleRevisions',
                data: {
                    title: (injector: $InjectorLike) => injector.get('article').name,
                    nav: false,
                },
                resolve: {
                    revisions: (article: Article, articleResource: ArticleResource) =>
                        articleResource.revisions({id: article.id}).$promise,
                },
            });
    })
    .run(($transitions: TransitionService) => {
        $transitions.onStart({to: 'article.search'}, (transition: Transition) => {
            const {['#']: _, ...params} = transition.params();
            // Do default search if we come from another state and no parameters are specified or their default value
            if (
                transition.from().name !== 'search' &&
                !Object.keys(params).some((p) => params[p] !== transition.to().params[p].value)
            ) {
                return transition.router.stateService.target(transition.to(), {defaultSearch: true});
            }

        });
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
