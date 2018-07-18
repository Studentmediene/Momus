import * as angular from 'angular';

/* @ngInject */
class InlineEditCtrl {

}

export default angular
    .module('momusApp.components.inlineEdit', [])
    .component('inlineEdit', {
        controller: InlineEditCtrl,
        controllerAs: 'vm',
        template: require('./inlineEdit.html'),
    });
