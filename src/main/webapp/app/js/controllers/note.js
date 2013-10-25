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

        // The scope of this controller is the note panel,
        // which also falls under the control of the article controller

        $scope.saveNote = function () {
            $http.put('/api/note', $scope.note).success(function (data) {
                $scope.note = data;
                $scope.originalNote = angular.copy($scope.note);
                $scope.noteIsDirty = false;
            });
        };
        // the note that belongs to an article object is a string called note
        // the note that belongs to a user is a note object with a string called content
        $scope.$watch('note.content', function (newVal, oldVal) {
            $scope.noteIsDirty = !angular.equals($scope.note, $scope.originalNote);
        });
        $scope.noteRules = noteParserRules;

        $http.get('/api/note').success(function (data) {
            $scope.note = data;
            $scope.originalNote = angular.copy($scope.note);
        });
    });