/*
* Copyright 2013 Studentmediene i Trondheim AS
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
    .controller('MetaCtrl', function ($scope, ArticleService) {

        // The scope of this controller is the note panel,
        // which also falls under the control of the article controller
        // so this controller has access to the ArticleCtrl $scope

        $scope.toggleEditMode = function() {
            $scope.metaEditMode = !$scope.metaEditMode;
        };

        $scope.addPerson = function(id, list) {
            if (id == null || id == "" || ArticleService.listOfPersonsContainsID($scope.article[list], id)) {
                return;
            }
            ArticleService.getPerson(id, function(data) {
                $scope.article[list].push(data);
            });
        };

        $scope.removePerson = function (person, list) {
            ArticleService.removeFromArray($scope.article[list], person);
        };

        $scope.saveMeta = function() {
            var updates = ArticleService.updateObject($scope.article);
            if (ArticleService.changed("name", $scope)) {
                updates.updated_fields.push("name");
            }
            if (ArticleService.changed("journalists", $scope)) {
                updates.updated_fields.push("journalists");
            }
            if (ArticleService.changed("photographers", $scope)) {
                updates.updated_fields.push("photographers");
            }
            ArticleService.updateArticle(updates, $scope, function() {
                $scope.metaEditMode = false;
            });
        };

        $scope.cancelMeta = function() {
            ArticleService.revert("name", $scope);
            ArticleService.revert("journalists", $scope);
            ArticleService.revert("photographers", $scope);
            $scope.metaEditMode = false;
        };
    });