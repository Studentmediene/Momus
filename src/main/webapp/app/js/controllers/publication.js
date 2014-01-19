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
    .controller('PublicationCtrl', function ($scope, $http) {

        $scope.newWindow = 'false';
        $scope.publicationView = 'false';
        $scope.publicationData = null;
        $scope.publication = null;
        $scope.date = new Date();
        $scope.yearSelected = $scope.date.getFullYear();


        /**
         * Saving all the publications to an array within the year given as parameter.
         *
         * @method getPublicationByYear
         * @param year. From what year you want to see publications.
         *
         */
        $scope.getPublicationByYear = function(year) {
            $http.get('/api/publication/year/' + year)
                .success(function (data) {
                    $scope.publicationData = data;
                    console.log($scope.publicationData.length);
                })
                .error(function() {
                    console.log("Error retrieving publications by year");
                })
        };

        $scope.getPublicationByYear($scope.yearSelected);

        /**
         * Saving all the active publications from right now, and ten years ahead.
         *
         * @method getActivePublications
         */
        $scope.getActivePublications = function() {
            $http.get('/api/publication/activePublications')
                .success(function (data) {
                    $scope.publicationData = data;
//                    console.log($scope.publicationData.length);
                })
                .error(function() {
                    console.log("Error retrieving active publications");
                })

        };

        /**
         * Saving a publication to the publication-variable. The index parameter gives the publication in the
         * publicationData array.
         *
         * @method getPublication
         * @param index. The index of a given publication in the publicationData array
         */
        $scope.getPublication = function(index) {
            $scope.publication = $scope.publicationData[index];
        };

        $scope.new = {
            name: '',
            release_date: ''
        };

        //Using this to show the years in the dropdown menu, but this definitely needs a change to make it independent!
        $scope.hardCodedYears = [

            {year: '2011'},
            {year: '2012'},
            {year: '2013'},
            {year: '2014'}
        ];


        /**
         * This method saves either a new publication or an already existing publication. If it is a new publication,
         * the new publication is pushed to the publicationData array and to the database. If it is an already existing
         * publication, it will replace the old data with the new data, and update the database.
         *
         * @method fileSaved
         * @param index. The index of a given publication in the publicationData array. Only used when updating a publication.
         */
        $scope.fileSaved = function(index) {
            if ($scope.newWindow == 'true') {
                $http.post('/api/publication', $scope.new)
                    .success(function(newPublication) {
                        $scope.publicationData.push(newPublication);
                        console.log("vellykket .POST");
                        alert("Publikasjon lagret!");
                    })
                    .error(function() {
                        console.log("Ikke vellykket");
                    });
            }

            else if ($scope.publicationView=='true') {
                console.log("publication: " + $scope.publication);
                $http.put('/api/publication/' + index, $scope.publication)
                    .success(function(savedPublication) {
                        //Does a "replaceObject" method exist?
                        //var index = $scope.publicationData.indexOf(id);
                        $scope.publicationData[index] = savedPublication;
                        console.log("Vellykket .PUT");
                        alert("Publikasjon lagret!");
                    })
                    .error(function() {
                        console.log("Ikke vellykket");
                    });
            }
            else {
                console.log("nothing happened..");
            }

        };

    });


