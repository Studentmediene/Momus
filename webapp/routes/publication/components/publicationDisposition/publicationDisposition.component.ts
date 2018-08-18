import * as angular from 'angular';

/* @ngInject */
class PublicationDispositionCtrl {
}

export default angular.module('momusApp.routes.publication.publicationDisposition', [])
    .component('publicationDisposition', {
        bindings: {
            persons: '<',
        },
        controller: PublicationDispositionCtrl,
        controllerAs: 'vm',
        template: require('./publicationDisposition.html'),
    });
