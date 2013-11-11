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
    .controller('MetaCtrl', function ($scope, $http) {

        // The scope of this controller is the note panel,
        // which also falls under the control of the article controller
        // so this controller has access to the ArticleCtrl $scope

        $scope.toggleEditMode = function() {
            $scope.metaEditMode = !$scope.metaEditMode;
        };

        var listOfPersonsContainsID = function(list, id) {
            var i;
            for (i = 0; i < list.length; i++) {
                // Note that this compares the object's integer ID to the string id
                if (list[i].id == id) {
                    return true;
                }
            }
            return false;
        };

        var removeFromArray = function(array, object) {
            var index = array.indexOf(object);
            if (index > -1) {
                array.splice(index, 1);
            }
        };

        $scope.addJournalist = function(newJournalistID){
            if (listOfPersonsContainsID($scope.article.journalists, newJournalistID)){
                return;
            }
            $http.get('/api/person/' + newJournalistID).success( function(data) {
                $scope.article.journalists.push(data);
                $scope.journalistsDirty = true;
            });
        };

        $scope.removeJournalist = function(journalist) {
            removeFromArray($scope.article.journalists, journalist);
            $scope.journalistsDirty = true;
        };

        $scope.addPhotographer = function(newPhotographerID){
            if (listOfPersonsContainsID($scope.article.photographers, newPhotographerID)){
                return;
            }
            $http.get('/api/person/' + newPhotographerID).success( function(data) {
                $scope.article.photographers.push(data);
                $scope.photographersDirty = true;
            });
        };

        $scope.removePhotographer = function(photographer) {
            removeFromArray($scope.article.photographers, photographer);
            $scope.photographersDirty = true;
        };

        $scope.saveMeta = function() {
            var updates = $scope.newUpdatesObject();
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
            $scope.putUpdates(updates);
            $scope.metaEditMode = false;
        };

        $scope.cancelMeta = function() {
            $http.get('/api/article/' + $scope.article.id).success( function(data) {
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