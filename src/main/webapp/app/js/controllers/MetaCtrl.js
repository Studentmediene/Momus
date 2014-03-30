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
    .controller('MetaCtrl', function ($scope, $http, ArticleService) {

        // The scope of this controller is the note panel,
        // which also falls under the control of the article controller
        // so this controller has access to the ArticleCtrl $scope

        $scope.persons = [];
        $scope.personLookup = {};

        var format = function(id) {
//            console.log(id);
            var person = $scope.personLookup[id];
//            console.log($scope.personLookup);
            return person.first_name + " " + person.last_name;
        };

        $scope.select2Options = {
            'multiple': true,
            'simple_tags': false,
            tags: function () { // wrapped in a function so it sees changes to $scope.tags
                return $scope.persons;
            },
//            createSearchChoice: function () {
//                return null; // only use pre-defined tags
//            },
            data: function() {
                return {
                    text: format,
                    results: $scope.persons
                }
            },
            formatSelection: format,
            formatResult: format
        };

        $scope.toggleEditMode = function() {
            $scope.metaEditMode = !$scope.metaEditMode;

            if ( $scope.metaEditMode ) {
                $http.get('/api/person').success(function(data) {
                    $scope.persons = data;

                    angular.forEach(data, function(object) {
                        $scope.personLookup[object.id] = object;
                    });
                });

                $scope.journalistIDs = $scope.article.journalists.map( function(person) {
                    return person.id;
                });
            }
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
                $scope.article.journalists = ArticleService.deserializeJSON($scope.article.journalists);
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