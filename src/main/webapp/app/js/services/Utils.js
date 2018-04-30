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

angular.module('momusApp.services')
    .filter('trustHtml', ['$sce', function($sce){
        return function(text) {
            return $sce.trustAsHtml(text);
        };
    }])
    .filter('diffsToHtml', ['$sce', function($sce){
        return function(diffs, type){
            var html = "";
            for (var i = 0; i < diffs.length; i++) {
                if (diffs[i].operation == "DELETE" && type == "del") {
                    html += "<del>";
                    html += diffs[i].text;
                    html += "</del>";
                } else if (diffs[i].operation == "DELETETAG" && type == "del") {
                    html += diffs[i].text.slice(0, -1) + " class=\"del\">"; // add a del class
                } else if (diffs[i].operation == "EQUAL") {
                    html += diffs[i].text;
                } else if (diffs[i].operation == "INSERT" && type == "add") {
                    html += "<ins>";
                    html += diffs[i].text;
                    html += "</ins>";
                } else if (diffs[i].operation == "INSERTTAG" && type == "add") {
                    html += diffs[i].text.slice(0, -1) + " class=\"ins\">"; //add a ins class
                }
            }

            return $sce.trustAsHtml(html);
        };
    }])
    .filter('startFrom', function() {
        return function(list, start){
            start = +start;
            return list.slice(start);
        }
    })
    .filter('initials', function() {
        return function(name) {
            return name
                .split(' ')
                .filter((e, i, a) => i === 0 || i === a.length -1)
                .map(e => e[0])
                .join('')
        }
    })
    .factory('expandColorCode', function() {
        return color => "#" + color.split("#")[1].split("").map(x => x+x).join("");
    });