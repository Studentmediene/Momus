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
    .service('ViewArticleService', function ($cookies) {
        return {

            checkRecentlyViewed: function(articleId){
                var currentViewed = $cookies.getObject("recentlyViewed");
                var duplicateIndex = currentViewed.indexOf(articleId);

                while(duplicateIndex >= 0) {
                    currentViewed.splice(duplicateIndex, 1);
                    duplicateIndex = currentViewed.indexOf(articleId);
                }
                if(currentViewed.length > 4){
                    currentViewed.splice(0, currentViewed.length-4);
                }
                currentViewed.push(articleId);
                return currentViewed;
            },

            viewArticle : function(articleId){
                if($cookies.getObject("recentlyViewed")) {
                    var updated = this.checkRecentlyViewed(articleId);
                    $cookies.putObject("recentlyViewed", updated);
                } else {
                    $cookies.putObject("recentlyViewed", [articleId]);
                }

            },

            getRecentViews : function(){
                const recents = $cookies.getObject("recentlyViewed") || [];
                recents.reverse(); // We want newest first
                return recents;
            }
        };
    });