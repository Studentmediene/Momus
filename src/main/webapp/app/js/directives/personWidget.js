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
    directive( 'personWidget', ['$rootScope', function ($rootScope) {
        return {
            restrict: 'A',
            scope:true,
            template: '<div class="person-widget-element" ng-show="isVisible"><div class="popover-title">{{pw_person.full_name}}<span ng-click="togglePW()" class="closePw pull-right"><i class="fa fa-times"></i></span></div><div class="popover-content"><table><tr><td><b>E-post:</b></td> <td><a href="mailto:{{person.email}}">{{pw_person.email}}</a></td></tr><tr><td><b>Telefon:</b></td><td>{{pw_person.phone_number}}</td></tr></table><div class="arrow"></div></div></div><span ng-click="togglePW()" class="person-widget-btn" ng-transclude></span>',
            transclude: true,
            link: function(scope, element, attrs){
                scope.isVisible = false;
                element.css({position : 'relative'});

                if(attrs.personWidget){
                    scope.pw_person = scope[attrs.personWidget];
                } else {
                    scope.pw_person = scope.person;
                }

                scope.togglePW = function(){
                    var visi = scope.isVisible;
                    $rootScope.$broadcast('closePersonWidgets');
                    scope.isVisible = !(visi);
                };

                $rootScope.$on('closePersonWidgets', function(){
                    scope.isVisible = false;
                });
            }
        };
    }]
);