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
    .service('ArticleService', function ($http) {
        return {
            // Getting stuff
            getArticle: function (id) {
                return $http.get('/api/article/' + id);
            },
            getContent: function (id) {
                return $http.get('/api/article/' + id + '/content');
            },
            getMultiple: function(ids) {
                return $http.get('/api/article/multiple?' + ids.map(id => "id=" + id).join("&"));
            },
            search: function (searchObject) {
                return $http.post('/api/article/search', searchObject);
            },
            getRevisions: function (id) {
                return $http.get('/api/article/' + id + '/revisions');
            },
            getDiffs: function (articleId, revId1, revId2) {
                return $http.get('/api/article/' + articleId + '/revisions/' + revId1 + '/' + revId2);
            },

            // Editing stuff
            updateMetadata: function (article) {
                return $http.patch('/api/article/' + article.id + '/metadata', article);
            },

            updateNote: function (article) {
                return $http.patch('/api/article/' + article.id + '/note', JSON.stringify(article.note));
            },
            createNewArticle: function (article) {
                return $http.post('/api/article', article);
            },
            deleteArticle: function(article){
                return $http.patch('/api/article/' + article.id + '/archived?archived=true');
            },
            restoreArticle: function(article){
                return $http.patch('/api/article/' + article.id + '/archived?archived=false');
            },


            // Getting metadata, cache everything
            getTypes: function () {
                return $http.get('/api/article/types', {cache: true});
            },

            getStatuses: function () {
                return $http.get('/api/article/statuses', {cache: true});
            },

            getSections: function () {
                return $http.get('/api/article/sections', {cache: true});
            },

            getReviews: function () {
                return $http.get('/api/article/reviews', {cache: true});
            }
        };
    })
    .factory('Article', $resource => {
        return $resource('/api/article/:id/:resource', 
            {
                id: '@id'
            },
            {
                content: { method: 'GET', params: {resource: 'content'} },
                revisions: { method: 'GET', url: '/api/article/:id/revisions/:rev1/:rev2', isArray: true },
                multiple: { method: 'GET', params: {id: 'multiple'}, isArray: true },                
                search: { method: 'POST', params: {id: 'search'}, isArray: true },
                updateMetadata: { method: 'PATCH', params: {resource: 'metadata'} },
                updateNote: { method: 'PATCH', params: { resource: 'note'} },
                archive: { method: 'PATCH', params: {archived: true} },
                restore: { method: 'PATCH', params: {archived: false} },

                types: { method: 'GET', params: {id: 'types'}, isArray: true },
                statuses: { method: 'GET', params: {id: 'statuses'}, isArray: true },
                reviewStatuses: { method: 'GET', params: {id: 'reviews'}, isArray: true },
                sections: { method: 'GET', params: {id: 'sections', isArray: true }},
                statusCounts: { method: 'GET', params: {id: 'statuscounts'}, isArray: true },
                reviewStatusCounts: { method: 'GET', params: {id: 'reviewstatuscounts'}, isArray: true },
            });
    });
