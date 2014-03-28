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
    .controller('PublicationCtrl', function ($scope, $http) {

        $scope.publications = [];
        var today = new Date();
        $scope.yearSelected = "Aktive utgaver";
        $scope.yearsInDropdown = [];

        $scope.editing = {};

        $scope.dateOptions = {// Needed for the date picker, always start weeks on a monday
            'starting-day': 1
        };


        $scope.getPublications = function (year) {
            $scope.yearSelected = year;

            if (year === "Aktive utgaver") {
                getActivePublications();
            } else {
                getPublicationsByYear(year);
            }
        };


        $scope.getPublications($scope.yearSelected);
        calculateYearsInDropdownMenu();


        $scope.editPublication = function (publication) {
            $scope.editing = angular.copy(publication); // always work on a copy

            // clear form errors
            if (!$scope.editing.release_date) {
                $scope.editing.release_date = '';
            }
            $scope.publicationForm.$setPristine();
        };

        function getPublicationsByYear(year) {
            $http.get('/api/publication/year/' + year)
                .success(function (data) {
                    $scope.publications = data;
                });
        }

        function getActivePublications() {
            $http.get('/api/publication/activePublications')
                .success(function (data) {
                    $scope.publications = data;
                });
        }


        function calculateYearsInDropdownMenu() {
            var sinceYearX = 2009;

            for (var i = today.getFullYear(); i > sinceYearX; i--) {
                $scope.yearsInDropdown.push(i);
            }
        }


        /**
         * This method saves either a new publication or an already existing publication.
         * All publications are then reloaded
         */
        $scope.saveEditedPublication = function () {
            $scope.isSaving = true;
            if (!$scope.editing.id) { // no id means it's a new one
                $http.post('/api/publication', $scope.editing)
                    .success(function (savedPublication) {
                        $scope.editing = savedPublication;
                        $scope.getPublications($scope.yearSelected); // reload all
                        $scope.isSaving = false;
                    });
            } else { // it's an old one
                $http.put('/api/publication/' + $scope.editing.id, $scope.editing)
                    .success(function (savedPublication) {
                        $scope.editing = savedPublication;
                        $scope.getPublications($scope.yearSelected); // reload all
                        $scope.isSaving = false;
                    });
            }
        };


    });


