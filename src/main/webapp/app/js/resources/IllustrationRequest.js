'use strict';

angular.module('momusApp.resources')
    .factory('IllustrationRequest', $resource => {
        return $resource('/api/illustrationrequests/:id/:resource',
            {
                id: '@id'
            },
            {
                get: { method: 'GET', bypassInterceptor: true },
                createExternal: { method: 'POST', params: {id: 'external'} },
                mine: { method: 'GET', params: {id: 'mine'}, isArray: true },
                updateStatus: { method: 'PATCH', params: {resource: 'status'}, hasBody: false },
                updateIllustratorComment: { method: 'PATCH', params: {resource: 'illustratorcomment'} },
                updateRequest: { method: 'PATCH', params: {resource: 'request'} }
            })
    })
    .constant('illustrationRequestStatuses', {
        PENDING: {color: '#fff76b', name: 'Venter'},
        ACCEPTED: {color: '#94ff7a', name: 'Godkjent'},
        DENIED: {color: '#ffd1d1', name: 'Avvist'},
        COMPLETED: {color: '#d8d3ff', name: 'Gjort'}
    });