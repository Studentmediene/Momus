'use strict';

angular.module('momusApp.controllers')
    .controller('IllustrationsCtrl', function(
        IllustrationRequest,
        illustrationRequestStatuses,
        $uibModal
    ) {
        const vm = this;

        vm.statusData = illustrationRequestStatuses;
        vm.requests = IllustrationRequest.query({status: 'PENDING'});

        vm.updateStatus = updateStatus;

        vm.saveIllustratorComment = saveIllustratorComment;
        vm.giveToIllustrators = giveToIllustrators;

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

        function giveToIllustrators(request) {
            $uibModal.open({
                templateUrl: 'partials/article/editIllustratorsModal.html',
                controller: 'EditIllustratorsModalCtrl',
                resolve: {
                    article: request.article,
                }
            }).result.then(() => updateStatus(request, 'ACCEPTED'));
        }
    });