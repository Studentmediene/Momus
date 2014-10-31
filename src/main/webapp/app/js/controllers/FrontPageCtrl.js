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
    .controller('FrontPageCtrl', function ($scope, NoteService, noteParserRules, PersonService, ArticleService, SavedSearchService, $location) {
        $scope.noteRules = noteParserRules;

        NoteService.getNote().success(function (data) {
            $scope.note = data;
            $scope.unedited = angular.copy(data);
        });

        $scope.saveNote = function () {
            $scope.savingNote = true;
            NoteService.updateNote($scope.note).success(function (data) {
                $scope.note = data;
                $scope.unedited = angular.copy(data);
                $scope.savingNote = false;
            });
        };

        $scope.loadingArticles = true;
        PersonService.getCurrentUser().success(function(user){
            $scope.user = user;
            ArticleService.search({"persons": [user.id]}).success(function (articles) {
                $scope.loadingArticles = false;
                $scope.myArticles = articles;
                if($scope.myArticles.length <= 0 ){
                    $scope.noArticles = true;
                }
            });
        });
        function update(){
            SavedSearchService.getSavedSearches().success(function (data) {
                $scope.savedSearches = data;
                if($scope.savedSearches.length <= 0){
                    $scope.noSearches = true;
                }else{
                    for(var i=0;i<$scope.savedSearches.length;i++){
                        $scope.savedSearches[i].description = JSON.parse($scope.savedSearches[i].description);
                    }
                }
            });
        }
        update();

        $scope.gotosearch = function(search){
            $location.url("artikler/" + search.url);
        };

        $scope.deleteS = function(id){
            SavedSearchService.deleteSearch(id).success(function (data){
                update();
            })
        };

        $scope.$on('$locationChangeStart', function (event) {
            if (promptCondition()) {
                if (!confirm("Er du sikker på at du vil forlate siden? Det finnes ulagrede endringer.")) {
                    event.preventDefault();
                }
            }
        });

        window.onbeforeunload = function () {
            if (promptCondition()) {
                return "Det finnes ulagrede endringer.";
            }
        };

        $scope.$on('$destroy', function() {
            window.onbeforeunload = undefined;
        });

        function promptCondition() {
            return $scope.unedited.content != $scope.note.content;
        }
    });