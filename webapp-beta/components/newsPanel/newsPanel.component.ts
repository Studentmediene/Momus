import * as angular from 'angular';

export default angular.module('momusApp.components.newsPanel', [])
    .component('newsPanel', {
        template: require('./newsPanel.html'),
        controllerAs: 'vm',
        bindings: {
            news: '<',
        }
    })