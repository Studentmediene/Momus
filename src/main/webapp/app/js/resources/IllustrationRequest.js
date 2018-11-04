'use strict';

angular.module('momusApp.resources')
    .factory('IllustrationRequest', (momResource) => {
        return momResource('/api/illustrationrequests/:id/:resource',
            {
                id: '@id'
            },
            {
                get: { method: 'GET', bypassInterceptor: true },
                createExternal: { method: 'POST', params: {id: 'external'} },
                mine: { method: 'GET', params: {id: 'mine'}, isArray: true },
                updateStatus: { method: 'PATCH', params: {resource: 'status'}, hasBody: false },
                updateIllustratorComment: { method: 'PATCH', params: {resource: 'illustratorcomment'}, skipTransform: true },
                updateRequest: { method: 'PATCH', params: {resource: 'request'} }
            },
            requestTransform,
            responseTransform)
    })
    .constant('illustrationRequestStatuses', {
        PENDING: {color: '#fff76b', name: 'Venter'},
        ACCEPTED: {color: '#d8d3ff', name: 'Godkjent'},
        DENIED: {color: '#ffd1d1', name: 'Avvist'},
        COMPLETED: {color: '#94ff7a', name: 'Gjort'}
    });

function requestTransform(illustrationRequest) {
    if(!illustrationRequest) return illustrationRequest;
    return illustrationRequest;

}

function responseTransform(illustrationRequest) {
    if(!illustrationRequest) return illustrationRequest;
    return {
        ...illustrationRequest,
        created: new Date(illustrationRequest.created),
        due_date: new Date(illustrationRequest.due_date),
    }
}