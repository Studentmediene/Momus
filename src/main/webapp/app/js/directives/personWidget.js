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
    directive( 'personWidget', ['$rootScope', '$injector', function ($rootScope, $injector) {
        return {
            restrict: 'A',
            scope:true,
            template: '<span class="hidden-xs"><div class="person-widget-element" ng-show="isVisible"><div class="popover-title">{{pw_person.full_name}}<span ng-click="togglePW()" class="closePw pull-right"><i class="fa fa-times"></i></span></div><div class="popover-content"><table><tr><td><b>E-post:</b></td> <td><a href="mailto:{{person.email}}">{{pw_person.email}}</a></td></tr><tr><td><b>Telefon:</b></td><td>{{pw_person.phone_number}}</td></tr></table><div class="arrow"></div></div></div><span ng-click="togglePW()" class="person-widget-btn" ng-transclude></span></span>'
            + '<span class="visible-xs"><span ng-click="openModal()" class="person-widget-btn" ng-transclude></span></span>',
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

                scope.openModal = function(){
                    var messageModal = $injector.get("MessageModal");
                    messageModal.info('<table class="table"><tr><td>Navn:</td><td>'+ scope.pw_person.full_name + '</td></tr><tr><td>Email:</td><td><a href="mailto:' + scope.pw_person.email + '">' + scope.pw_person.email + '</a></td></tr><tr><td>Telefon:</td><td>' + scope.pw_person.phone_number + '</td></tr></table>');
                };

                $rootScope.$on('closePersonWidgets', function(){
                    scope.isVisible = false;
                });
            }
        };
    }]
);