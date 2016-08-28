'use strict';

angular.module('momusApp.controllers')
    .controller('CreateArticleModalCtrl', function($scope, $modalInstance, PublicationService, ArticleService, PersonService){
        $scope.article = {
            name: "",
            journalists: null,
            photographers: null,
            comment: "",
            publication: null,
            type: null,
            status: null,
            section: null,
            content: "",
            use_illustration: false,
            external_author: '',
            external_photographer: '',
            quote_check_status: false
        };

        PublicationService.getAll().success(function (data) {
            $scope.publications = data;
            $scope.article.publication = PublicationService.getActive(data);
            if((!typeof pubId === 'undefined')){
                for(var i = 0; i < $scope.publications.length;i++){
                    if($scope.publications[i].id == pubId){
                        $scope.article.publication = $scope.publications[i];
                        break;
                    }
                }
            }
        });

        ArticleService.getStatuses().success(function (data) {
            $scope.statuses = data;
            $scope.article.status = data[0];
        });

        ArticleService.getTypes().success(function (data) {
            $scope.types = data;
        });

        ArticleService.getSections().success(function (data) {
            $scope.sections = data;
            $scope.article.section = data[0];
        });

        PersonService.getAll().success(function (data) {
            $scope.persons = data;
        });

        $scope.photoTypes = [{value: false, name: 'Foto'}, {value: true, name: 'Illustrasjon'}];
        $scope.quoteCheckTypes = [{value: false, name: 'I orden'}, {value: true, name: 'Trenger sitatsjekk'}];


        $scope.createArticle = function () {
            $scope.creating = true;
            ArticleService.createNewArticle($scope.article).success(function (data) {
                $scope.creating = false;
                $modalInstance.close(data.id);
            });
        };

        $scope.cancel = function(){
            $modalInstance.dismiss('cancel');
        }
    }).value('pubId',null);