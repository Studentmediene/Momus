import * as angular from 'angular';
import { StateProvider, StateService } from '@uirouter/angularjs';

import { Person } from '../../models/Person';

import navigation from './navigation.component';

export interface NavItem {
    stateName: string;
    icon: string;
    name: string;
}

export default angular
    .module('momusApp.routes.navigation', [
        navigation.name,
    ])
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
    });
