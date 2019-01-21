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

angular.module('momusApp.directives').
    directive( 'searchSortButton', ['$rootScope', function ($rootScope) {
        return {
            restrict: 'A',
            scope:true,
            templateUrl:"/assets/partials/templates/searchSortButton.html",
            transclude:true,
            link: function(scope, element, attrs){
                scope.type = attrs.searchSortButton;
                if('switchSortDirection' in attrs){
                    scope.switchDir = attrs.switchSortDirection;
                }else{
                    scope.switchDir = false;
                }

                element.on("click", function(){
                    scope.vm.sortSearch(scope.type, scope.switchDir);
                });
            }
        };
    }]
);