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
    .controller('PublicationCtrl', function ($scope, $http, PublicationService, $templateRequest, MessageModal) {

        var today = new Date();

        $scope.viewYear = today.getFullYear();
        $scope.publications = [];
        $scope.yearsInDropdown = [];

        $scope.currentPage = 1;
        $scope.pubsPerPage = 10 ;
        $scope.numPubs = 0;
        $scope.slicedPublications = [];

        $scope.editing = {};

        $scope.dateOptions = {// Needed for the date picker, always start weeks on a monday
            'starting-day': 1
        };


        PublicationService.getAll().success(function (data) {
            $scope.publications = data;
            $scope.yearChanged();
        });

        $scope.yearFilter = function (publication) {
            if($scope.viewYear == "Alle"){
                return true;
            }
            else{
                return publication.release_date && publication.release_date.indexOf($scope.viewYear) != -1;
            }
        };

        $scope.yearChanged = function(){
            $scope.setPublicationSlice();
        };

        $scope.pageChanged = function(){
            $scope.setPublicationSlice();
        };

        $scope.needPagination = function(){
            return $scope.numPubs > $scope.pubsPerPage;
        };

        $scope.setPublicationSlice = function(){
            $scope.publications.sort(function(a,b){
                return new Date(b.release_date) - new Date(a.release_date);
            });

            //Filter for year
            $scope.slicedPublications = $scope.publications.filter(function(pub){
                return $scope.yearFilter(pub);
            });
            $scope.numPubs = $scope.slicedPublications.length;

            //Filter for page
            $scope.slicedPublications = $scope.slicedPublications.slice((($scope.currentPage-1)*$scope.pubsPerPage), (($scope.currentPage)*$scope.pubsPerPage));

        };

        calculateYearsInDropdownMenu();

        function calculateYearsInDropdownMenu() {
            var sinceYearX = 2009;

            for (var i = today.getFullYear(); i > sinceYearX; i--) {
                $scope.yearsInDropdown.push(i);
            }
        }

        $scope.editPublication = function (publication) {
            $scope.editing = angular.copy(publication); // always work on a copy
            $scope.editingId = publication.id;

            // clear form errors
            if (!$scope.editing.release_date) {
                $scope.editing.release_date = '';
            }
            $scope.publicationForm.$setPristine();
        };

        /**
         * This method saves either a new publication or an already existing publication.
         */
        $scope.saveEditedPublication = function () {
            $scope.isSaving = true;
            if (!$scope.editing.id) { // no id means it's a new one
                console.log("whaat");
                PublicationService.createNew($scope.editing)
                    .success(function (savedPublication) {
                        $scope.publications.push(savedPublication);
                        $scope.editPublication(savedPublication);
                        $scope.isSaving = false;
                        $scope.setPublicationSlice();
                    });
            } else { // it's an old one
                PublicationService.updateMetadata($scope.editing)
                    .success(function (savedPublication) {
                        for(var i = 0; i < $scope.publications.length;i++){
                            if($scope.publications[i].id == $scope.editingId){
                                $scope.publications[i] = savedPublication;
                                console.log($scope.publications[i])
                            }
                        }
                        $scope.editPublication(savedPublication);
                        $scope.isSaving = false;
                        $scope.setPublicationSlice();
                    });
            }
        };

        $scope.showHelp = function(){
            $templateRequest("partials/templates/help/publicationHelp.html").then(function(template){
                MessageModal.info(template);
            })
        }

    });


