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
                return $http.get('/api/publication');
            },
            getById: function(id) {
                return $http.get('/api/publication/' + id);
            },
            createNew: function(publication) {
                return $http.post('/api/publication', publication);
            },
            updateMetadata: function(publication) {
                return $http.put('/api/publication/metadata', publication);
            },
            getActive: function() {
                return $http.get('/api/publication/active');
            },
            getPages: function(id) {
                return $http.get('/api/publication/pages/'+id);
            },
            createPage: function(publication, pagenr, layout_status) {
                return {
                    page_nr: pagenr,
                    note: "",
                    advertisement: false,
                    articles: [],
                    publication: publication.id,
                    layout_status: layout_status
                };
            },
            deletePage: function(id) {
                return $http.delete('/api/publication/pages/delete/' + id);
            },
            generateDisp: function(id) {
                return $http.get('/api/publication/pages/generate/'+id);
            },
            getLayoutStatuses: function(){
                return $http.get('/api/publication/layoutstatus')
            },
            getStatusCounts: function(id){
                return $http.get('/api/article/statuscount/' + id)
            },
            getLayoutStatusCounts: function(id){
                return $http.get('/api/publication/statuscount/' + id)
            },
            getReviewStatusCounts: function(id){
                return $http.get('/api/article/reviewstatuscount/' + id)
            },
            linkPagesToArticles: function(pages, articles){
                for(var i = 0; i < pages.length; i++){
                    var page = pages[i];
                    for(var j = 0; j < page.articles.length; j++){
                        var article = page.articles[j];
                        for(var k = 0; k < articles.length; k++){
                            if(articles[k].id == article.id){
                                pages[i].articles[j] = articles[k];
                                break;
                            }
                        }
                    }
                }
            }
        };
    });