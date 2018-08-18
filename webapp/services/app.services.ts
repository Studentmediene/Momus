import * as angular from 'angular';
import momResourceFactory from 'services/momResource.factory';

import tips, { Tip } from './tips';

export type RandomTip = () => Tip;

export default angular.module('momusApp.services', [])
    .factory('momResource', momResourceFactory)
    .factory('randomTip', () => () => (
        tips[Math.floor(Math.random() * tips.length)]
    ));
