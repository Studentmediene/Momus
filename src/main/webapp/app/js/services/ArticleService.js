/*
 * Copyright 2013 Studentmediene i Trondheim AS
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
    .service('ArticleService', function($http){
        return {
            "updateObject": function(obj) {
                // When the server receives this object,
                // it will overwrite the server data for each listed field and leave the others unchanged
                return {
                    "object": obj,
                    "updated_fields": []
                };
            },
            "updateArticle": function (updates) {
                $http.put('/api/article/update', updates)
                    .success( function (data) {

                    }
                )
                    .error( function () {
                        alert("Oops, Momus fikk ikke til å lagre til serveren.");
                    }
                );
            },
            "getArticle": function(id, success) {
                $http.get('/api/article/' + id)
                    .success( function(data) {
                        success(data)
                    }
                );
            },
            "getPerson": function(id, success) {
                $http.get('/api/person/' + id)
                    .success( function (data) {
                        success(data)
                    }
                );
            },
            "listOfPersonsContainsID": function(list, id) {
                for (var i = 0; i < list.length; i++) {
                    // Note that this compares the object's integer ID to the string id
                    if (list[i].id == id) {
                        return true;
                    }
                }
                return false;
            },
            "removeFromArray": function(array, object) {
                var index = array.indexOf(object);
                if (index > -1) {
                    array.splice(index, 1);
                }
            }
        }
    });