import * as angular from 'angular';

import NavigationCtrl from './navigation.ctrl';
import { Person } from '../../models/Person';
import { StateProvider, StateService } from '@uirouter/angularjs';

export interface NavItem {
    stateName: string;
    icon: string;
    name: string;
}

export default angular
    .module('momusApp.routes.navigation', [])
    .config(($stateProvider: StateProvider) => {
        $stateProvider.state('root', {
            component: 'navigation',
            abstract: true,
            resolve: {
                user: (loggedInUser: Person) => loggedInUser,
                navItems: ($state: StateService) => $state.get()
                    .filter((state) => state.data && state.data.nav)
                    .map((state): NavItem => ({stateName: state.name, ...state.data.nav})),
            },
        });
    })
    .component('navigation', {
        bindings: {
            user: '<',
            navItems: '<',
        },
        controller: NavigationCtrl,
        controllerAs: 'vm',
        template: require('./navigation.html'),
    });
