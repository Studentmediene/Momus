import * as angular from 'angular';
import momResourceFactory from './momResource.factory';
import openModal from './openModal.factory';
import sessionService from './session.service';
import tips, { Tip } from './tips';

export type RandomTip = () => Tip;

export default angular.module('momusApp.services', [])
    .factory('momResource', momResourceFactory)
    .factory('openModal', openModal)
    .factory('randomTip', () => () => (
        tips[Math.floor(Math.random() * tips.length)]
    ))
    .service('sessionService', sessionService);
