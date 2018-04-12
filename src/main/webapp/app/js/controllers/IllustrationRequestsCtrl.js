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

        vm.editIllustratorComment = editIllustratorComment;
        vm.cancelIllustratorComment = cancelIllustratorComment;
        vm.saveIllustratorComment = saveIllustratorComment;

        function updateStatus(request) {
            request.$updateStatus({status: request.status});
        }

        function editIllustratorComment(scope) {
            scope.editComment = true;
            scope.oldComment = scope.request.illustrator_comment;
        }

        function cancelIllustratorComment(scope) {
            scope.editComment = false;
            scope.request.illustrator_comment = scope.oldComment;
        }

        function saveIllustratorComment(scope) {
            scope.editComment = false;
            console.log(scope.request);
            IllustrationRequest.updateIllustratorComment(
                {id: scope.request.id},
                JSON.stringify(scope.request.illustrator_comment));
        }
    });