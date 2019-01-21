import * as angular from 'angular';

export default angular.module('momusApp.routes.publication.dispositionAdvert', [])
    .component('dispositionAdvert', {
        template: require('./dispositionAdvert.html'),
        controllerAs: 'vm',
        bindings: {
            advert: '<',
            columnWidths: '<',
        },
    });
