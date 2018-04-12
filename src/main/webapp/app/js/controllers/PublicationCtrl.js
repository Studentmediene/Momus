/*
 * Copyright 2016 Studentmediene i Trondheim AS
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
    .controller('PublicationCtrl', function ($scope, $templateRequest, MessageModal, Publication) {
        const vm = this;

        vm.publications = [];

        vm.yearOptions = [];
        vm.selectedYear = new Date().getFullYear();

        vm.currentPage = 1;
        vm.pageSize = 10;

        vm.editing = {};

        vm.dateOptions = {// Needed for the date picker, always start weeks on a monday
            'starting-day': 1
        };

        vm.isInCurrentYear = isInCurrentYear;
        vm.editPublication = editPublication;
        vm.saveEditedPublication = saveEditedPublication;

        vm.showHelp = showHelp;

        getPublications();

        function getPublications() {
            vm.publications = Publication.query({}, function() {
                vm.yearOptions = createYearOptions();
            });
        }

        function getOldestPublication() {
            return vm.publications.sort(function(a, b) { return a.release_date > b.release_date; })[0];
        }

        function isInCurrentYear(publication) {
            if(vm.selectedYear === "Alle"){
                return true;
            }
            else{
                return publication.release_date && new Date(publication.release_date).getFullYear() === vm.selectedYear;
            }
        }

        function createYearOptions() {
            const oldest = getOldestPublication();
            const sinceYearX = oldest && new Date(oldest.release_date).getFullYear() || 2009;
            const years = ["Alle"];

            for (let i = new Date().getFullYear(); i >= sinceYearX; i--) {
                years.push(i);
            }

            return years;
        }

        function editPublication(publication) {
            vm.editing = angular.copy(publication); // always work on a copy

            // clear form errors
            if (!vm.editing.release_date) {
                vm.editing.release_date = '';
            }
            $scope.publicationForm.$setPristine();
        }

        function saveEditedPublication() {
            vm.isSaving = true;
            if (vm.editing.id === undefined) { // no id means it's a new one
                const publication = Publication.save({}, vm.editing, function() {
                    vm.publications.push(publication);
                    vm.editPublication(publication);
                    vm.isSaving = false;
                })
            } else { // it's an old one
                const updatedIndex = vm.publications.findIndex(function(publication) { return publication.id === vm.editing.id});
                const updatedPublication = Publication.update({}, vm.editing, function() {
                    vm.publications[updatedIndex] = updatedPublication;
                    vm.editPublication(updatedPublication);
                    vm.isSaving = false;
                })
            }
        }

        function showHelp(){
            $templateRequest("partials/templates/help/publicationHelp.html").then(function(template){
                MessageModal.info(template);
            });
        }
    });