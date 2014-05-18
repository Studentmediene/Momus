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
    .controller('SourceTagsCtrl', function ($scope, $http, $q) {
        $scope.tags = [];
        $scope.originalTags = [];
        $scope.isSaving = {}; // holds true/false for if the given tag is currently saving, for displaying loading button

        getTagsFromServer();

        $scope.isChanged = function (tag, index) {
            return tag.tag != $scope.originalTags[index].tag;
        };

        $scope.updateTag = function (newTag, index) {
            var oldTag = $scope.originalTags[index];
            var tagToSave = {tag: newTag.tag}; // creating a new object, as the original one may have "unused" values set

            $scope.isSaving[index] = true;

            $http.put('/api/source/tag/' + encodeURIComponent(oldTag.tag), tagToSave).success(function (data) {
                oldTag.tag = newTag.tag;
                $scope.isSaving[index] = false;
            });
        };

        $scope.deleteTag = function (index) {
            var tagToDelete = $scope.originalTags[index];
            var unused = $scope.tags[index].unused;

            if (!unused && !confirm("Vil du slette denne tagen og fjerne den fra alle kilder?")) {
                return;
            }

            $http.put('/api/source/tag/delete', tagToDelete).success(function (data) {
                // remove from array
                $scope.originalTags.splice(index, 1);
                $scope.tags.splice(index, 1);
            });
        };


        function getTagsFromServer() {
            var tagPromise = $http.get('/api/source/tag');
            var unusedPromise = $http.get('/api/source/tag/unused');

            tagPromise.success(function (data) {
                $scope.tags = data;
                $scope.originalTags = angular.copy(data);
            });
            unusedPromise.success(function (data) {
                $scope.unused = data;
            });

            $q.all([tagPromise, unusedPromise]).then(function () {
                markUnusedTags($scope.tags, $scope.unused);
            })
        }

        function markUnusedTags(allTags, unused) {
            for (var i = 0; i < unused.length; i++) {
                for (var j = 0; j < allTags.length; j++) {
                    if (unused[i].tag == allTags[j].tag) {
                        allTags[j]["unused"] = true;
                    }
                }
            }
        }

    });