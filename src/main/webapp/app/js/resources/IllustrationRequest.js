'use strict';

angular.module('momusApp.resources')
    .factory('IllustrationRequest', $resource => {
        return $resource('/api/illustrationrequests/:id/:resource',
            {
                id: '@id'
            },
            {
                createExternal: { method: 'POST', params: {id: 'external'} },
                mine: { method: 'GET', params: {id: 'mine'} },
                updateStatus: { method: 'PATCH', params: {resource: 'status'} }
            })
    });