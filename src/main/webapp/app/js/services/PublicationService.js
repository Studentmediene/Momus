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
    .service('PublicationService', function ($http) {
        return {
            getAll: function () {
                return $http.get('/api/publications');
            },
            getById: function(id) {
                return $http.get('/api/publications/' + id);
            },
            createNew: function(publication) {
                return $http.post('/api/publications', publication);
            },
            updateMetadata: function(publication) {
                return $http.put('/api/publications/' + publication.id, publication);
            },
            getActive: function() {
                return $http.get('/api/publications/active');
            },
            getPages: function(id) {
                return $http.get('/api/publications/' + id + '/pages');
            },
            createPage: function(publication, pagenr, layout_status) {
                var page = {
                    page_nr: pagenr,
                    note: "",
                    advertisement: false,
                    articles: [],
                    publication: publication.id,
                    layout_status: layout_status
                };
                return $http.post('/api/publications/' + publication.id + '/pages', page);
            },
            deletePage: function(pubid, id) {
                return $http.delete('/api/publications/'  + pubid + '/pages/' + id);
            },
            getStatusCounts: function(id){
                return $http.get('/api/article/statuscount/' + id);
            },
            getReviewStatusCounts: function(id){
                return $http.get('/api/article/reviewstatuscount/' + id);
            },
            linkPagesToArticles: function(pages, articles){
                for(var i = 0; i < pages.length; i++){
                    var page = pages[i];
                    for(var j = 0; j < page.articles.length; j++){
                        var article = page.articles[j];
                        for(var k = 0; k < articles.length; k++){
                            if(articles[k].id === article.id){
                                pages[i].articles[j] = articles[k];
                                break;
                            }
                        }
                    }
                }
            }
        };
    });