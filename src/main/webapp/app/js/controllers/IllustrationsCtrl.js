'use strict';

angular.module('momusApp.controllers')
    .controller('IllustrationsCtrl', function(
        IllustrationRequest,
        illustrationRequestStatuses
    ) {
        const vm = this;

        vm.statusData = illustrationRequestStatuses;
        vm.requests = IllustrationRequest.query({status: 'PENDING'});

        vm.updateStatus = updateStatus;

        vm.saveIllustratorComment = saveIllustratorComment;

        function updateStatus(request, status) {
            request.status = status;
            request.$updateStatus({status});
        }

        function saveIllustratorComment(request, comment) {
            request.comment = comment;
            IllustrationRequest.updateIllustratorComment(
                {id: request.id},
                JSON.stringify(comment));
        }
    });