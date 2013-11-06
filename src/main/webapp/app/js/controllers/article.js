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
    .controller('ArticleCtrl', function ($scope, $http, $routeParams, articleParserRules, noteParserRules) {

        var newUpdatesCopy = function() {
            var updates = angular.copy($scope.article);
            updates.updated_fields = [];
            return updates;
        };

        var putUpdates = function (updates) {
            $http.put('/api/article/update', updates)
                .success( function (data) {

                })
                .error( function () {
                    alert("Oops, Momus fikk ikke til å lagre til serveren.");
                });
        };

        // The scope of this controller is the entire article view

        // This initializes an object to be replaced by a GETed object.
        // This is to stop functions that needs it from complaining that the object doesn't exist
        // before the request is complete.
        $scope.article = { content: "" };

        $http.get('/api/article/' + $routeParams.id).success(function (data) {
            $scope.article = data;
            $scope.originalContent = angular.copy($scope.article.content);
            $scope.originalName = angular.copy($scope.article.name);
            $scope.originalNote = angular.copy($scope.article.note);
        });

        $scope.articleRules = articleParserRules;

        $scope.$watch('article.content', function () {
            $scope.articleIsDirty = !angular.equals($scope.article.content, $scope.originalContent);
        });

        $scope.saveArticle = function () {
            var updates = newUpdatesCopy();
            updates.updated_fields.push("content");
            putUpdates(updates);
            $scope.articleIsDirty = false;
        };

        // Note control

        $scope.saveNote = function () {
            var updates = newUpdatesCopy();
            updates.updated_fields.push("note");
            putUpdates(updates);
            $scope.noteIsDirty = false;
        };

        $scope.$watch('article.note', function () {
            $scope.noteIsDirty = !angular.equals($scope.article.note, $scope.originalNote);
        });

        $scope.noteRules = noteParserRules;

        // Metadata editing functionality

        $scope.toggleEditMode = function() {
            $scope.metaEditMode = !$scope.metaEditMode;
        }

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
            if (newJournalistID == "" || listOfPersonsContainsID($scope.article.journalists, newJournalistID)){
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
            if (newPhotographerID == "" || listOfPersonsContainsID($scope.article.photographers, newPhotographerID)){
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
            var updates = newUpdatesCopy();
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
            putUpdates(updates);
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

