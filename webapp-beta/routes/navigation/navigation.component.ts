import * as angular from 'angular';

import './navigation.scss';

export default angular.module('momusApp.routes.navigation.navigation', [])
    .component('navigation', {
        bindings: {
            user: '<',
            navItems: '<',
            session: '<',
        },
        controllerAs: 'vm',
        template: require('./navigation.html'),
    });
