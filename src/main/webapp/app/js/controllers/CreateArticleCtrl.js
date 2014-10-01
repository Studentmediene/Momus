/*
 * Copyright 2014 Studentmediene i Trondheim AS
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
    .controller('CreateArticleCtrl', function ($scope, PersonService, ArticleService, PublicationService, $location) {


        $scope.article = {
            name: "",
            journalists: null,
            photographers: null,
            comment: "",
            publication: null,
            type: null,
            status: null,
            content: ""

        };

        PublicationService.getAll().success(function (data) {
            $scope.publications = data;
            $scope.article.publication = data[0];
        });

        ArticleService.getStatuses().success(function (data) {
            $scope.statuses = data;
        });

        ArticleService.getTypes().success(function (data) {
            $scope.types = data;
        });

        PersonService.getAll().success(function (data) {
            $scope.persons = data;
        });

        $scope.renderPerson = PersonService.renderPerson;

        $scope.createArticle = function () {
            $scope.creating = true;
            ArticleService.createNewArticle($scope.article).success(function (data) {
                $location.path("/artikler/" + data.id);
            }).finally(function() {
                $scope.creating = false;
            });
        };

    });