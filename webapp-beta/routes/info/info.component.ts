import * as angular from 'angular';

/* @ngInject */
class InfoCtrl implements angular.IController {
    public pageSize: number = 5;
    public currentPage: number = 1;

    public news: any[] = [];
}

export default angular.module('momusApp.routes.info.infoPage', [])
    .component('infoPage', {
        controller: InfoCtrl,
        controllerAs: 'vm',
        template: require('./info.html'),
        bindings: {
            news: '<',
        },
    });
