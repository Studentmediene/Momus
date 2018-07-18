import * as angular from 'angular';
import * as ngresource from 'angular-resource';

import personResourceFactory from './person.resource';
import articleResourceFactory from './article.resource';

export default angular
    .module('momusApp.resources', [
        ngresource,
    ])
    .factory('personResource', personResourceFactory)
    .factory('articleResource', articleResourceFactory);
