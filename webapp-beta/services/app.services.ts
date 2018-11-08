import * as angular from 'angular';
import * as angularCookies from 'angular-cookies';

import momResourceFactory from './momResource.factory';
import openModal from './openModal.factory';
import sessionService from './session.service';
import tips, { Tip } from './tips';
import CookieService from './cookies.service';

export type RandomTip = () => Tip;

export default angular.module('momusApp.services', [
        angularCookies,
    ])
    .factory('momResource', momResourceFactory)
    .factory('openModal', openModal)
    .factory('randomTip', () => () => (
        tips[Math.floor(Math.random() * tips.length)]
    ))
    .service('sessionService', sessionService)
    .service('cookieService', CookieService)
    .config(($cookiesProvider: {defaults: ng.cookies.ICookiesOptions}) => {
        $cookiesProvider.defaults.path = '/';
    });
