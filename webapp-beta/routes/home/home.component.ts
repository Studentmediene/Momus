import * as angular from 'angular';

import { IController } from 'angular';
import { RandomTip } from 'services/app.services';
import { Tip } from 'services/tips';
import CookieService from 'services/cookies.service';

/* @ngInject */
class HomeCtrl implements IController {
    public randomTip: RandomTip;
    public tip: Tip;
    public cookieService: CookieService;
    public $window: angular.IWindowService;
    public $timeout: angular.ITimeoutService;

    constructor(randomTip: RandomTip, cookieService: CookieService, $window: angular.IWindowService) {
        this.randomTip = randomTip;
        this.tip = randomTip();

        this.cookieService = cookieService;
        this.$window = $window;
    }

    public notUseBeta() {
        this.cookieService.setUseBeta(false);
        this.$window.location.href = '/';
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
