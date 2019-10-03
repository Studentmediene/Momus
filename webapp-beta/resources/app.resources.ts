import * as angular from 'angular';
import * as ngresource from 'angular-resource';

import personResourceFactory from './person.resource';
import articleResourceFactory from './article.resource';
import advertResourceFactory from './advert.resource';
import publicationResourceFactory from './publication.resource';
import pageResourceFactory from './page.resource';
import newsItemResourceFactory from './newsItem.resource';
import staticValuesResourceFactory from './staticValues.resource';

export type ResourceFunc<Res, Body = null, Params = {}> = Body extends null
    ? (Params extends {}
        ? (params?: Params, success?: (res: Res) => void, error?: (err: any) => void) => Res
        : (params: Params, success?: (res: Res) => void, error?: (err: any) => void) => Res
    )
    : (params: Params, body: Body, success?: (res: Res) => void, error?: (err: any) => void) => Res;

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
    .factory('advertResource', advertResourceFactory)
    .factory('publicationResource', publicationResourceFactory)
    .factory('pageResource', pageResourceFactory)
    .factory('newsItemResource', newsItemResourceFactory)
    .factory('staticValuesResource', staticValuesResourceFactory);
