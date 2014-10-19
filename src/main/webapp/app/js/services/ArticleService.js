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
    .service('ArticleService', function ($http) {
        return {
            // Getting stuff
            getArticle: function (id) {
                return $http.get('/api/article/' + id);
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
                return $http.put('/api/article/metadata', article);
            },

            updateContent: function (article) {
                return $http.put('/api/article/content', article);
            },

            updateNote: function (article) {
                return $http.put('/api/article/note', article);
            },
            createNewArticle: function (article) {
                return $http.post('/api/article', article);
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
            }

        };
    });