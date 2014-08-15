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
    .controller('NavbarCtrl', function ($scope, $location, PersonService) {
        PersonService.getCurrentUser().success(function(user) {
            $scope.user = user;
        });

        $scope.isCollapsed = true;

        /*  Checks if the current path is equal to the provided location
            If location ends with %, it will count as a wildcard.
            For instance, /sources% will match /sources/40
            while         /sources will not match /sources/40
        */
        $scope.isActive = function(location) {
            if(location.substring(location.length - 1) === '%') { // If ending with a wildcard
                var start = location.substring(0, location.length - 1);
                return $location.path().substring(0, start.length) === start;
            }

            return location === $location.path();
        };
    });

