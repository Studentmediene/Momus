import * as angular from 'angular';

/* @ngInject */
class PersonWidgetCtrl {

}

export default angular
    .module('momusApp.components.personWidget', [])
    .component('personWidget', {
        bindings: {
            thing: '<',
        },
        controller: PersonWidgetCtrl,
        controllerAs:  'vm',
        template: require('./personWidget.html'),
    });
