'use strict';

angular.module('momusApp.controllers')
    .controller('IllustrationRequestsCtrl', function(
        IllustrationRequest,
        illustrationRequestStatuses
    ) {
        const vm = this;

        vm.statusData = illustrationRequestStatuses;
        vm.requests = IllustrationRequest.query({status: 'PENDING'});

        vm.updateStatus = updateStatus;

        function updateStatus(request) {
            request.$updateStatus({status: request.status});
        }
    });