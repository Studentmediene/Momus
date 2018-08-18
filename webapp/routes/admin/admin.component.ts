import * as angular from 'angular';

class AdminCtrl {

}

export default angular.module('momusApp.routes.admin.adminPage', [])
    .component('adminPage', {
        bindings: {
            user: '<',
        },
        template: require('./admin.html'),
        controller: AdminCtrl,
        controllerAs: 'vm',
    });
