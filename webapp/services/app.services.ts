import * as angular from 'angular';
import momResourceFactory from './momResource';

export default angular.module('momusApp.services', [])
    .factory('momResource', momResourceFactory);
