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
directive('scrollTrigger', function($rootScope, $window) {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var top = element[0].offsetTop - 51; // Because of navbar
                angular.element($window).bind("scroll", function() {
                    if(this.pageYOffset >= top) {
                        element.addClass(attrs.scrollTrigger);
                    } else {
                        element.removeClass(attrs.scrollTrigger);
                    }
                });


            }
        };
    }
);