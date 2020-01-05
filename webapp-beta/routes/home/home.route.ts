import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';
import { ArticleResource } from '../../resources/article.resource';
import { SectionResource } from '../../resources/section.resource';
import { Person } from '../../models/Person';

import homePage from './home.component';
import articleTable from './components/articleTable/articleTable.component';
import { NewsItemResource } from 'resources/newsItem.resource';
import { ArticleSearchParams } from 'models/ArticleSearchParams';
import CookieService from 'services/cookies.service';
import { emptyArrayResponse } from 'utils';

const routeModule = angular
    .module('momusApp.routes.home', [
        homePage.name,
        articleTable.name,
    ])
    .config(($stateProvider: StateProvider) => {
        $stateProvider.state('home', {
            parent: 'root',
            url: '/',
            component: 'homePage',
            data: {
                title: 'Hjem',
                nav: {
                    name: 'Hjem',
                    icon: 'fas fa-home',
                },
            },
            resolve: {
                myArticles: (articleResource: ArticleResource, loggedInUser: Person) =>
                    articleResource.lastArticlesForPerson({ userId: loggedInUser.id }),
                sectionArticles: (articleResource: ArticleResource, loggedInUser: Person) => {
                    if (loggedInUser.section == null) {
                        return emptyArrayResponse();
                    }
                    const params = new ArticleSearchParams();
                    params.page_number = 1;
                    params.page_size = 9;
                    params.section = loggedInUser.section;
                    return articleResource.search({}, params.stringify());
                },
                lastViewedArticles: (articleResource: ArticleResource, cookieService: CookieService) => {
                    const recents = cookieService.getRecentArticles();
                    return recents.length > 0
                        ? articleResource.multiple({ids: recents})
                        : emptyArrayResponse();
                },
                news: (newsItemResource: NewsItemResource) =>
                    newsItemResource.query().$promise,
                sections: (sectionResource: SectionResource) =>
                    sectionResource.query().$promise,
            },
        });
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
