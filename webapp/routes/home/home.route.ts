import * as angular from 'angular';

import HomeCtrl from './home.ctrl';
import { Environment } from '../../app.types';
import { StateProvider } from '@uirouter/angularjs';
import { ArticleResource } from '../../resources/article.resource';
import { Person } from '../../models/Person';

const routeModule = angular
    .module('momusApp.routes.home', [])
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
    })
    .component('homePage', {
        bindings: {
            user: '<',
            myArticles: '<',
        },
        template: require('./home.html'),
        controller: HomeCtrl,
        controllerAs: 'vm',
    });

export default {
    shouldLoadRoute: (env: Environment) => true,
    module: routeModule,
};
