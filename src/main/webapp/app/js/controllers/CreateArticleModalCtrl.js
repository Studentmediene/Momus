/*
 * Copyright 2016 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

angular.module('momusApp.controllers')
    .controller('CreateArticleModalCtrl', function($scope, $uibModalInstance, Publication, ArticleService, PersonService, $q, pubId){
        $scope.article = {
            name: "",
            journalists: null,
            photographers: null,
            comment: "",
            publication: null,
            type: null,
            status: null,
            section: null,
            review: null,
            content: "",
            use_illustration: false,
            external_author: '',
            external_photographer: '',
            quote_check_status: false
        };

        $q.all([Publication.query(), Publication.active()]).then(function (data) {
            $scope.publications = data[0];
            $scope.article.publication = data[1];

            // Set the publication if one was passed into the modal
            if(pubId){
                for(var i = 0; i < $scope.publications.length;i++){
                    if($scope.publications[i].id === pubId){
                        $scope.article.publication = $scope.publications[i];
                        break;
                    }
                }
            }
        });

        ArticleService.getStatuses().then(function (data) {
            $scope.statuses = data.data;
            $scope.article.status = data.data[0];
        });

        ArticleService.getTypes().then(function (data) {
            $scope.types = data.data;
        });

        ArticleService.getSections().then(function (data) {
            $scope.sections = data.data;
            $scope.article.section = data.data[0];
        });

        ArticleService.getReviews().then(function (data){
            $scope.article.review = data.data[0];
        });

        PersonService.getAll().then(function (data) {
            $scope.persons = data.data;
        });

        $scope.photoTypes = [{value: false, name: 'Foto'}, {value: true, name: 'Illustrasjon'}];
        $scope.quoteCheckTypes = [{value: false, name: 'I orden'}, {value: true, name: 'Trenger sitatsjekk'}];


        $scope.createArticle = function () {
            $scope.creating = true;
            ArticleService.createNewArticle($scope.article).then(function (data) {
                $scope.creating = false;
                $uibModalInstance.close(data.data.id);
            });
        };

        $scope.cancel = function(){
            $uibModalInstance.dismiss('cancel');
        };

    }).value('pubId',null);