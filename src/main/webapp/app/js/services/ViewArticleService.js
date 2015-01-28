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

angular.module('momusApp.services')
    .service('ViewArticleService', function ($cookieStore) {
        return {

            checkRecentlyViewed: function(newValue){
                var temp_array = $cookieStore.get("recentlyViewed");
                var duplicateIndex = temp_array.indexOf(newValue);
                while(duplicateIndex >= 0) {
                    temp_array.splice(duplicateIndex, 1);
                    duplicateIndex = temp_array.indexOf(newValue);
                }
                if(temp_array.length > 4){
                    temp_array.splice(0, temp_array.length-4);
                }
                temp_array.push(newValue);
                return temp_array;
            },

            viewArticle : function(article){
                //$cookieStore.remove("recentlyViewed");
                if($cookieStore.get("recentlyViewed")) {
                    $cookieStore.put("recentlyViewed", this.checkRecentlyViewed(article));
                } else {
                    $cookieStore.put("recentlyViewed", [article]);
                }

            },

            getRecentViews : function(){
                return $cookieStore.get("recentlyViewed");
            }
        }
    });