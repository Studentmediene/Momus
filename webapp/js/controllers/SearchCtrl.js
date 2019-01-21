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
    .controller('SearchCtrl', function (
        $scope,
        $location,
        $templateRequest,
        $uibModal,
        $state,
        $stateParams,
        Article,
        MessageModal,
        activePublication,
        publications,
        persons,
        sections,
        statuses,
        reviews,
        searchParams,
        results
    ) {
        const vm = this;

        vm.publications = publications;
        vm.persons = persons;
        vm.sections = sections;
        vm.statuses = statuses;
        vm.reviews = reviews;
        vm.hasNextPage = results.length > searchParams.page_size;
        vm.results = vm.hasNextPage ? results.slice(0, results.length-1) : results;
        vm.searchParams = searchParams;
        vm.articleSortReverse = false;
        vm.articleSortType = "[publication.release_date,section.name]";

        vm.createArticle = createArticle;
        vm.sortSearch = sortSearch;
        vm.search = search;
        vm.showHelp = showHelp;

        function search(pageDelta) {
            if (pageDelta) {
                vm.searchParams.page_number = parseInt(vm.searchParams.page_number, 10) + pageDelta; // parse, as suddenly it's a string!
            }
            $state.go('.', vm.searchParams, {inherit: false});
        }

        function sortSearch(type, switchDir){
            if(vm.articleSortType != type){
                vm.articleSortReverse = switchDir;
                vm.articleSortType = type;
            } else {
                vm.articleSortReverse = !vm.articleSortReverse;
            }

            $scope.$apply();
        }

        function createArticle() {
            $uibModal.open({
                templateUrl: '/assets/partials/article/createArticleModal.html',
                controller: 'CreateArticleModalCtrl'
            }).result.then(id => $state.go('article', {id: id}));
        }

        function showHelp() {
            $templateRequest("/assets/partials/templates/help/searchHelp.html").then(function(template){
                MessageModal.info(template);
            });
        }
    });