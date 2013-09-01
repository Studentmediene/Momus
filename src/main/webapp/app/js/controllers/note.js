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
    .controller('NoteCtrl', function ($scope, $http, noteParserRules) {

        $http.get('/api/note').success(function (data) {
            $scope.note = data;
            $scope.original = angular.copy($scope.note);
        });

        $scope.rules = noteParserRules;

        $scope.$watch('note.content', function (newVal, oldVal) {
            $scope.isDirty = !angular.equals($scope.note, $scope.original);
        });

        $scope.saveNote = function () {
            $http.put('/api/note', $scope.note).success(function (data) {
                $scope.note = data;
                $scope.original = angular.copy($scope.note);
                $scope.isDirty = false;
            })
        };

    });