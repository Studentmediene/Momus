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

angular.module('momusApp.services')
    .filter('trustHtml', ['$sce', function($sce){
        return function(text) {
            return $sce.trustAsHtml(text);
        };
    }])
    .filter('diffsToHtml', ['$sce', function($sce){
        return function(diffs, type){
            var html = "";
            for(var i = 0; i < diffs.length;i++){
                if((diffs[i].operation == "DELETE" || diffs[i].operation == "DELETETAG") && type=="del"){
                    html+="<del style=\"background:#ffe6e6;\">";
                    html+=diffs[i].text;
                    html+="</del>";
                }else if(diffs[i].operation == "EQUAL"){
                    html+=diffs[i].text;
                }else if((diffs[i].operation == "INSERT" || diffs[i].operation == "INSERTTAG") && type=="add"){
                    html+="<ins style=\"background:#e6ffe6;\">";
                    html+=diffs[i].text;
                    html+="</ins>";
                }
            }
            return $sce.trustAsHtml(html);
        }
    }]);