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
    directive( 'personWidget', ['$tooltip', '$templateCache', '$http', '$compile', function ($tooltip, $templateCache, $http, $compile) {
        return {
            restrict: 'AE',
            scope:true,
            link: function(scope, element, attrs){
                $http.get('partials/templates/personWidget.html').success(function(data){
                    scope.hidePopover = function(){
                        $(element).popover("hide");
                    };

                    $(element).popover({
                        html: true,
                        placement: 'top',
                        trigger: trigger,
                        delay : {"show": 0, "hide":0},
                        title: person.full_name,
                        content: content,
                        template:$compile(data)(scope)
                    });
                });

                element.css({
                    borderBottom: '1px dotted #ccc',
                    cursor: 'pointer'
                });

                element.attr('tabindex', 0);

                if(attrs.personWidget){
                    var person = scope[attrs.personWidget];
                } else {
                    var person = scope.person;
                }

                if(attrs.pwTrigger){
                    var trigger = attrs.pwTrigger;
                } else {
                    var trigger = "click";
                }

                var content =
                        "<table>" +
                        "<tr><td><b>E-post:</b></td> <td><a href='mailto:" + person.email + "'>" + person.email + "</a></td></tr>" +
                        "<tr><td><b>Telefon:</b></td><td> " + person.phone_number + "</td></tr>" +
                        "</table>"
            }
        };
    }]
);