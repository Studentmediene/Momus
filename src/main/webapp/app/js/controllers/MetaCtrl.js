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

        $scope.addJournalist = function(newJournalistID){
            if (ArticleService.listOfPersonsContainsID($scope.article.journalists, newJournalistID)){
                return;
            }
            ArticleService.getPerson(newJournalistID, function(data) {
                $scope.article.journalists.push(data);
                $scope.journalistsDirty = true;
            });
        };

        $scope.removeJournalist = function(journalist) {
            ArticleService.removeFromArray($scope.article.journalists, journalist);
            $scope.journalistsDirty = true;
        };

        $scope.addPhotographer = function(newPhotographerID){
            if (ArticleService.listOfPersonsContainsID($scope.article.photographers, newPhotographerID)){
                return;
            }
            ArticleService.getPerson(newPhotographerID, function(data) {
                $scope.article.photographers.push(data);
                $scope.photographersDirty = true;
            });
        };

        $scope.removePhotographer = function(photographer) {
            ArticleService.removeFromArray($scope.article.photographers, photographer);
            $scope.photographersDirty = true;
        };

        $scope.saveMeta = function() {
            var updates = ArticleService.updateObject($scope.article);
            if ($scope.article.name != $scope.originalName) {
                updates.updated_fields.push("name");
                $scope.originalName = $scope.article.name;
            }
            if ($scope.journalistsDirty) {
                updates.updated_fields.push("journalists");
                $scope.journalistsDirty = false;
            }
            if ($scope.photographersDirty) {
                updates.updated_fields.push("photographers");
                $scope.photographersDirty = false;
            }
            ArticleService.updateArticle(updates);
            $scope.metaEditMode = false;
        };

        $scope.cancelMeta = function() {
            ArticleService.getArticle($scope.article.id, function(data) {
                if ($scope.journalistsDirty) {
                    $scope.article.journalists = data.journalists;
                    $scope.journalistsDirty = false;
                }
                if ($scope.photographersDirty) {
                    $scope.article.photographers = data.photographers;
                    $scope.photographersDirty = false;
                }
            });
            $scope.metaEditMode = false;
        };
    });