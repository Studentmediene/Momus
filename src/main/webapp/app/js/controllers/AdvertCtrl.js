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
    .controller('AdvertCtrl', function (
        $scope,
        Advert,
        Publication,
        TitleChanger,
        MessageModal,
        $routeParams,
        $timeout,
        $templateRequest
    ) {
        const vm = this;

        vm.metaEditMode = false;

        vm.metaClicked = () => { vm.metaEditMode ? saveMeta() : editMeta() };
        vm.cancelMeta = () => { vm.metaEditMode = false; };

        vm.showHelp = showHelp;

        // Get data
        vm.advert = Advert.get({id: $routeParams.id}, advert => {
            TitleChanger.setTitle(advert.name);
        });

        /* meta panel */
        function saveMeta() {
            vm.savingMeta = true;
            vm.metaEditing.$updateMetadata({}, updatedAdvert => {
                vm.advert = updatedAdvert;
                vm.savingMeta = false;
                vm.metaEditMode = false;
                TitleChanger.setTitle(vm.advert.name);
            });
        }

        function editMeta() {
            vm.metaEditMode = true;
            vm.metaEditing = angular.copy(vm.advert);

            if (!vm.publications) {
                vm.publications = Publication.query();
            }
        }

        function showHelp() {
            $templateRequest('partials/templates/help/advertHelp.html').then(function(template){
                MessageModal.info(template);
            });
        }

        function promptCondition() {
            return vm.metaEditMode === true;
        }

        $scope.$on('$locationChangeStart', (event) => {
            if (promptCondition()) {
                if (!confirm("Er du sikker på at du vil forlate siden? Det finnes ulagrede endringer.")) {
                    event.preventDefault();
                }
            }
        });
        $scope.$on('$destroy', () => { window.onbeforeunload = undefined; });

        window.onbeforeunload = function(){
            if(promptCondition()){
                return "Det finnes ulagrede endringer.";
            }
        };
    });
