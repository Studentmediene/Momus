import * as angular from 'angular';

import './navigation.scss';
import { TransitionService } from '@uirouter/core';

import * as UIKit from 'uikit';

class NavigationCtrl implements angular.IController {
    private $transitions: TransitionService;

    constructor($transitions: TransitionService) {
        this.$transitions = $transitions;
    }
    public $postLink() {
        const navDropdown = UIKit.dropdown('#navbardropdown', {offset: 0, mode: 'click', delayHide: 0});

        this.$transitions.onBefore({}, () => {
            navDropdown.hide();
        });
    }
}

export default angular.module('momusApp.routes.navigation.navigation', [])
    .component('navigation', {
        bindings: {
            user: '<',
            navItems: '<',
            session: '<',
        },
        controller: NavigationCtrl,
        controllerAs: 'vm',
        template: require('./navigation.html'),
    })
    .component('momusLogo', {
        template: // html
        `
        <a ui-sref="home" class="momus-logo uk-navbar-item" style="align-items: {{vm.align || 'end'}}">
            <span class="uk-logo">Momus</span>
            <span class="beta-label">beta</span>
            <div ng-if="vm.hideBackground == null" class="logo-bg"></div>
        </a>
        `,
        bindings: {
            hideBackground: '@',
            align: '@',
        },
        controllerAs: 'vm',
    });
