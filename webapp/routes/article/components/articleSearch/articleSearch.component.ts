import * as angular from 'angular';

import ArticleSearchCtrl from './articleSearch.ctrl';

import './articleSearch.scss';

export default angular.module('momusApp.routes.article.articleSearch', [])
    .component('articleSearch', {
        controller: ArticleSearchCtrl,
        controllerAs: 'vm',
        template: require('./articleSearch.html'),
        bindings: {
            results: '<',
            activePublication: '<',
            publications: '<',
            persons: '<',
            sections: '<',
            statuses: '<',
            reviews: '<',
            searchParams: '<',
            types: '<',
        },
});
