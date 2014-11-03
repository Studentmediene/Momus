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

            viewArticle : function(title, article){
                //$cookieStore.remove("recentlyViewed");
                //$cookieStore.remove("recentlyViewedNames");
                var articles = $cookieStore.get("recentlyViewed");
                var articleNames = $cookieStore.get("recentlyViewedNames");
                console.log(articles);
                if(!articles){
                    articles = [article];
                    articleNames = [title]
                } else if($.inArray(article, articles) > -1){
                    articles.splice($.inArray(article, articles),1);
                    articleNames.splice($.inArray(title, articleNames), 1);
                    articles.push(article);
                    articleNames.push(title);
                } else if(articles >= 10){
                    articles.shift().push(article);
                    articleNames.shift().push(title)
                } else {
                    articles.push(article);
                    articleNames.push(title);
                }
                $cookieStore.put("recentlyViewed", articles);
                $cookieStore.put("recentlyViewedNames", articleNames);
                console.log($cookieStore.get("recentlyViewed"));
                console.log($cookieStore.get("recentlyViewedNames"));
            },

            getRecentViews : function(){
                return $cookieStore.get("recentlyViewed");
            },

            getRecentNames : function(){
                return $cookieStore.get("recentlyViewedNames");
            }
        }
    });