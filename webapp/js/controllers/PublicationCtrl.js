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
    .controller('PublicationCtrl', function ($scope, $templateRequest, MessageModal, Publication, publications) {
        const vm = this;

        vm.publications = publications;

        vm.selectedYear = new Date().getFullYear();
        vm.currentPage = 1;
        vm.pageSize = 10;
        vm.dateOptions = {// Needed for the date picker, always start weeks on a monday
            'startingDay': 1
        };

        vm.createYearOptions = createYearOptions;
        vm.isInCurrentYear = isInCurrentYear;
        vm.editPublication = editPublication;
        vm.saveEditedPublication = saveEditedPublication;

        vm.showHelp = showHelp;

        function isInCurrentYear(publication) {
            return vm.selectedYear === 'Alle' || publication.release_date.getFullYear() === vm.selectedYear;
        }

        function createYearOptions() {
            return vm.publications.reduce((years, pub) => {
                const year = pub.release_date.getFullYear();
                return years.includes(year) ? years : years.concat(year);
            }, ["Alle"]);
        }

        function editPublication(publication) {
            vm.editing = angular.copy(publication); // always work on a copy

            $scope.publicationForm.$setPristine(); // clear form errors
        }

        function saveEditedPublication() {
            vm.isSaving = true;
            if (vm.editing.id == null) { // no id means it's a new one
                Publication.save({}, vm.editing, (publication) => {
                    vm.publications.push(publication);
                    vm.editPublication(publication);
                    vm.isSaving = false;
                })
            } else { // it's an old one
                const updatedIndex = vm.publications.findIndex(pub => pub.id === vm.editing.id);
                vm.editing.$updateMetadata({}, (updated) => {
                    vm.publications[updatedIndex] = updated;
                    editPublication(updated);
                    vm.isSaving = false;
                });
            }
        }

        function showHelp(){
            $templateRequest("/assets/partials/templates/help/publicationHelp.html").then(function(template){
                MessageModal.info(template);
            });
        }
    });