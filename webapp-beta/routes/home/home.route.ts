import * as angular from 'angular';

import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';
import { ArticleResource } from '../../resources/article.resource';
import { Person } from '../../models/Person';

import homePage from './home.component';

const routeModule = angular
    .module('momusApp.routes.home', [
        homePage.name,
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
                    articleResource.search({}, {persons: [loggedInUser.id], page_size: 9}).$promise,
            },
        });
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
