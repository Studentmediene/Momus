'use strict';

angular.module('momusApp.controllers')
    .controller('EditIllustratorsModalCtrl', function($scope, $uibModalInstance, Person, Article, article){
        $scope.article = article;
        $scope.persons = Person.query();
        $scope.addIllustrators = () => {
            Article.updateMetadata({}, $scope.article, () => {
                $uibModalInstance.close();
            });
        }

        $scope.cancel = () => {
            $uibModalInstance.dismiss('cancel');
        }
    })