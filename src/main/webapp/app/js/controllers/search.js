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
    .controller('SearchCtrl', function ($scope, $http, PersonService) {

        $scope.data = [];
        $scope.search = {
            free: '',
            status: '',
            persons: [],
            section: '',
            publication: ''
        };


        PersonService.getAll().success(function (data) {
            $scope.persons = data;
        });


        /**
         * Fetching publications from server
         */
        $http.get('/api/publication/year/2014')
            .success(function (data) {
                $scope.publications = data;
            });





        // TODO get these from server
        $scope.sections = [
            {id: 1, name:"Nyhet"},
            {id: 2, name:"Debatt"},
            {id: 3, name:"Kultur"},
            {id: 4, name:"Forbruker"},
            {id: 5, name:"Reportasje"},
            {id: 6, name:"Sport"}
        ];

        $scope.statuses = [
            {id: 1, name: "Skrives"},
            {id: 2, name: "Desking"},
            {id: 3, name: "Korrektur"},
            {id: 4, name: "Publisert"}
        ];

        $scope.searchFunc = function() {
            $http.post('/api/search', $scope.search)
                .success(function(data) {
                    $scope.data = data;
                });
        }
    });