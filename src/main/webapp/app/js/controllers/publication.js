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

        $scope.publications = [
            {month: '09', year: '2013'},
            {month: '10', year: '2013'},
            {month: '05', year: '2013'},
            {month: '11', year: '2013'},
            {month: '12', year: '2013'},
            {month: '06', year: '2013'},
            {month: '07', year: '2013'},
            {month: '08', year: '2013'},
            {month: '09', year: '2013'},
            {month: '06', year: '2012'}
        ];

        $scope.actives = [
            {month: '05', year: '2013'},
            {month: '09', year: '2013'},
            {month: '09', year: '2013'},
            {month: '06', year: '2012'}
        ];

        $scope.option = 'År';

        $scope.fileSaved = function() {
            alert("Utgave lagret!");
        }




    });


