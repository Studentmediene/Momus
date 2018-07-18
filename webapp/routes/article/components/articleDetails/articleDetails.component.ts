import * as angular from 'angular';

import ArticleDetailsCtrl from './articleDetails.ctrl';

import './articleDetails.scss';

export default angular
    .module('momusApp.routes.article.articleDetails', [])
    .component('articleDetails', {
        controller: ArticleDetailsCtrl,
        controllerAs: 'vm',
        template: require('./articleDetails.html'),
        bindings: {
            article: '<',
            articleContent: '<',
            sections: '<',
            types: '<',
            reviews: '<',
            statuses: '<',
            persons: '<',
        },
    });
