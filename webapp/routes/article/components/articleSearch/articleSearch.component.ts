import * as angular from 'angular';

/* @ngInject */
class ArticleSearchCtrl {

}

export default angular.module('momusApp.routes.article.articleSearch', [])
    .component('articleSearch', {
        controller: ArticleSearchCtrl,
        controllerAs: 'vm',
        template: require('./articleSearch.html'),
});
