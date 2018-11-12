'use strict';

angular.module('momusApp.controllers')
    .controller('RequestIllustrationModalCtrl', function($scope, $uibModalInstance, IllustrationRequest, article) {
        $scope.request = {
            description: "",
            number_of_illustrations: 1,
            number_of_pages: 1,
            article: article
        };

        $scope.createRequest = () => {
            IllustrationRequest.save({}, $scope.request, () => {
                $scope.creating = false;
                $uibModalInstance.close();
            })
        };

        $scope.cancel = function(){
            $uibModalInstance.close(false);
        };
    });