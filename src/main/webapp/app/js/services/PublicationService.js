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
            getActive: function(publications) {
                var today = new Date();

                var active = new Date(publications[0].release_date);
                var activeIndex = 0;
                for(var i = 1; i < publications.length;i++){
                    var date = new Date(publications[i].release_date);
                    if(date < active && date > today){
                        active = date;
                        activeIndex = i;
                    }
                }
                return publications[activeIndex];
            },
            getPages: function(id) {
                return $http.get('/api/publication/pages/'+id);
            },
            createPage: function(page) {
                return $http.post('/api/publication/pages/', page);
            },
            deletePage: function(id) {
                return $http.delete('/api/publication/pages/delete/' + id);
            },
            generateDisp: function(id) {
                return $http.get('/api/publication/pages/generate/'+id);
            },
            getLayoutStatuses: function(){
                return $http.get('/api/publication/layoutstatus')
            }
        };
    });