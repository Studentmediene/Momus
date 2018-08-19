import * as angular from 'angular';
import momResourceFactory from './momResource.factory';
import httpInterceptor from './httpInterceptor.factory';
import openModal from 'services/openModal.factory';
import tips, { Tip } from './tips';

export type RandomTip = () => Tip;

export default angular.module('momusApp.services', [])
    .factory('momResource', momResourceFactory)
    .factory('httpInterceptor', httpInterceptor)
    .factory('openModal', openModal)
    .factory('randomTip', () => () => (
        tips[Math.floor(Math.random() * tips.length)]
    ));
