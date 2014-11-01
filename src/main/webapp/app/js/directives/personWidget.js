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

angular.module('momusApp.directives').
    directive( 'personWidget', ['$rootScope', '$injector', function ($rootScope, MessageModal) {
        return {
            restrict: 'A',
            scope:true,
            templateUrl: 'partials/templates/personWidget.html',
            transclude: true,
            link: function(scope, element, attrs){
                scope.isVisible = false;

                if(attrs.personWidget){
                    scope.pw_person = scope[attrs.personWidget];
                } else {
                    scope.pw_person = scope.person;
                }

                scope.togglePW = function(){
                    var oldValue = scope.isVisible;
                    $rootScope.$broadcast('closePersonWidgets');
                    scope.isVisible = !(oldValue);
                };

                scope.openModal = function(){
                    var pwMessage = '<table class="table table-condensed"><tr><td>Navn:</td><td>'+ scope.pw_person.full_name + '</td></tr><tr><td>Email:</td><td><a href="mailto:' + scope.pw_person.email + '">' + scope.pw_person.email + '</a></td></tr><tr><td>Telefon:</td><td>' + scope.pw_person.phone_number + '</td></tr></table>';
                    MessageModal.info(pwMessage);
                };

                $rootScope.$on('closePersonWidgets', function(){
                    scope.isVisible = false;
                });
            }
        };
    }]
);