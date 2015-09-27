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
    .controller('PublicationCtrl', function ($scope, $http, PublicationService) {

        var today = new Date();

        $scope.viewYear = today.getFullYear();
        $scope.publications = [];
        $scope.yearsInDropdown = [];

        $scope.editing = {};

        $scope.dateOptions = {// Needed for the date picker, always start weeks on a monday
            'starting-day': 1
        };


        PublicationService.getAll().success(function (data) {
            $scope.publications = data;
        });

        $scope.yearFilter = function (publication) {
            // todo remove check?
            return publication.release_date && publication.release_date.indexOf($scope.viewYear) != -1;
        };

        calculateYearsInDropdownMenu();


        $scope.editPublication = function (publication) {
            $scope.editing = angular.copy(publication); // always work on a copy
            $scope.editingIndex = $scope.publications.indexOf(publication);

            // clear form errors
            if (!$scope.editing.release_date) {
                $scope.editing.release_date = '';
            }
            $scope.publicationForm.$setPristine();
        };

        function calculateYearsInDropdownMenu() {
            var sinceYearX = 2009;

            for (var i = today.getFullYear(); i > sinceYearX; i--) {
                $scope.yearsInDropdown.push(i);
            }
        }


        /**
         * This method saves either a new publication or an already existing publication.
         */
        $scope.saveEditedPublication = function () {
            $scope.isSaving = true;
            if (!$scope.editing.id) { // no id means it's a new one
                PublicationService.createNew($scope.editing)
                    .success(function (savedPublication) {
                        $scope.editing = savedPublication;
                        $scope.publications.push(savedPublication);
                        $scope.isSaving = false;
                    });
            } else { // it's an old one
                PublicationService.updateMetadata($scope.editing)
                    .success(function (savedPublication) {
                        $scope.publications[$scope.editingIndex] = savedPublication;
                        $scope.editPublication(savedPublication);
                        $scope.isSaving = false;
                    });
            }
        };

    });


