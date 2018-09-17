import * as angular from 'angular';

import { Person } from '../../models/Person';
import { Article } from '../../models/Article';
import { IController } from 'angular';
import { RandomTip } from 'services/app.services';
import { Tip } from 'services/tips';

/* @ngInject */
class HomeCtrl implements IController {
    public user: Person;
    public myArticles: Article[];

    public randomTip: RandomTip;
    public tip: Tip;

    constructor(randomTip: RandomTip) {
        this.randomTip = randomTip;
        this.tip = randomTip();
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
