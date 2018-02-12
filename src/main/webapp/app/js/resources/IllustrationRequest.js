'use strict';

angular.module('momusApp.resources')
    .factory('IllustrationRequest', $resource => {
        return $resource('/api/illustrationrequests/:id/:resource',
            {
                id: '@id'
            },
            {
                createExternal: { method: 'POST', params: {id: 'external'} },
                mine: { method: 'GET', params: {id: 'mine'}, isArray: true },
                updateStatus: { method: 'PATCH', params: {resource: 'status'}, hasBody: false }
            })
    })
    .constant('illustrationRequestStatuses', {
        PENDING: {color: '#ffcc00', name: 'Venter'},
        ACCEPTED: {color: '#5cb85c', name: 'Godkjent'},
        DENIED: {color: '#d9534f', name: 'Avvist'},
        COMPLETED: {color: '#5bc0de', name: 'Gjort'}
    });