import * as angular from 'angular';

import { NavItem } from './navigation.route';
import { Person } from '../../models/Person';

import './navigation.scss';

/* @ngInject */
class NavigationCtrl implements angular.IController {
}

export default angular.module('momusApp.routes.navigation.navigation', [])
    .component('navigation', {
        bindings: {
            user: '<',
            navItems: '<',
        },
        controller: NavigationCtrl,
        controllerAs: 'vm',
        template: require('./navigation.html'),
    });
