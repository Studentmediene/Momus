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
    .controller('SearchCtrl', function ($scope, $http) {
        $http.get('/api/person').success(function(data) {
            $scope.persons = data;
            console.log(data);
        });

        $scope.articles = [
            {id: 1, name: "Nyhet1", section: "Nyhet", publication: "2013 1"},
            {id: 2, name: "Nyhet2", section: "Nyhet", publication: "2013 1"},
            {id: 3, name: "Nyhet3", section: "Nyhet", publication: "2013 2"},
            {id: 4, name: "Debatt1", section: "Debatt", publication: "2013 2"},
            {id: 5, name: "Kultur1", section: "Kultur", publication: "2013 3"},
            {id: 6, name: "Forbruker1", section: "Forbruker", publication: "2013 3"},
            {id: 7, name: "Reportasje1", section: "Reportasje", publication: "2013 4"},
            {id: 8, name: "Sport1", section: "Sport", publication: "2013 4"}
        ];

        $scope.publications = [
            {id: 1, name: "2013 1"},
            {id: 2, name: "2013 2"},
            {id: 3, name: "2013 3"},
            {id: 4, name: "2013 4"}
        ];

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

        $scope.searchParams = {
            status: "",
            section: "",
            search: "",
            publication: "",
            persons: []
        };

        $scope.predicate = "age"
    });