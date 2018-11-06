import * as angular from 'angular';

import { IController } from 'angular';
import { RandomTip } from 'services/app.services';
import { Tip } from 'services/tips';
import CookieService from 'services/cookies.service';
import { StateService } from '@uirouter/core';

/* @ngInject */
class HomeCtrl implements IController {
    public randomTip: RandomTip;
    public tip: Tip;
    public cookieService: CookieService;
    public $window: angular.IWindowService;
    public $timeout: angular.ITimeoutService;
    public $state: StateService;

    constructor(
        randomTip: RandomTip,
        cookieService: CookieService,
        $window: angular.IWindowService,
        $state: StateService,
    ) {
        this.randomTip = randomTip;
        this.tip = randomTip();

        this.cookieService = cookieService;
        this.$window = $window;
        this.$state = $state;
    }

    public notUseBeta() {
        this.cookieService.setUseBeta(false);
        this.$window.location.href = '/';
    }

    public search(searchText?: string) {
        if (searchText == null || searchText === '') {
            return;
        }
        this.$state.go('article.search', {free: searchText});
    }
}

export default angular.module('momusApp.routes.home.homePage', [])
    .component('homePage', {
        bindings: {
            user: '<',
            myArticles: '<',
            favouritesectionArticles: '<',
            lastViewedArticles: '<',
            news: '<',
            sections: '<',
        },
        template: require('./home.html'),
        controller: HomeCtrl,
        controllerAs: 'vm',
    });
