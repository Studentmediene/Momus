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
    .controller('SourceTagsCtrl', function ($scope, $http) {
        $scope.tags = [];
        $scope.unused = [];
        $scope.showUnused = false;

        $scope.originalTags = [];
        $scope.originalUnused= [];


        $scope.isChanged = function(tag, index) {
            if ($scope.showUnused) {
                return tag.tag != $scope.originalUnused[index].tag;
            } else {
                return tag.tag != $scope.originalTags[index].tag;
            }
        };

        $scope.updateTag = function(newTag, index) {
            var oldTag;

            if ($scope.showUnused) {
                oldTag = $scope.originalUnused[index];
            } else {
                oldTag = $scope.originalTags[index];
            }

            $http.put('/api/source/tag/' + oldTag.tag, newTag).success(function(data) {
                getTagsFromServer();
            });
        };

        $scope.deleteTag = function(index) {
            if (!confirm("Vil du slette denne tagen og fjerne den fra alle kilder?")) {
                return;
            }

            var oldTag;

            if ($scope.showUnused) {
                oldTag = $scope.originalUnused[index];
            } else {
                oldTag = $scope.originalTags[index];
            }

            $http.put('/api/source/tag/delete', oldTag).success(function(data) {
                getTagsFromServer();
            });
        };

        getTagsFromServer();

        function getTagsFromServer() {
            $http.get('/api/source/tag').success(function(data) {
                $scope.tags = data;
                $scope.originalTags = angular.copy(data);
            });
            $http.get('/api/source/tag/unused').success(function(data) {
                $scope.unused = data;
                $scope.originalUnused = angular.copy(data);
            });
        }

    });