import * as angular from 'angular';
import * as ngresource from 'angular-resource';

import personResourceFactory from './person.resource';
import articleResourceFactory from './article.resource';
import publicationResourceFactory from './publication.resource';

export default angular
    .module('momusApp.resources', [
        ngresource,
    ]).
    constant('RESOURCE_ACTIONS', {
        save: {method: 'POST'},
        get:    {method: 'GET'},
        query: {method: 'GET', isArray: true},
        update: {method: 'PUT'},
        delete: {method: 'DELETE'},
    }).
    config((
        $resourceProvider: ng.resource.IResourceServiceProvider,
        RESOURCE_ACTIONS: ng.resource.IActionHash,
    ) => {
        $resourceProvider.defaults.actions = RESOURCE_ACTIONS;
    })
    .factory('personResource', personResourceFactory)
    .factory('articleResource', articleResourceFactory)
    .factory('publicationResource', publicationResourceFactory);
