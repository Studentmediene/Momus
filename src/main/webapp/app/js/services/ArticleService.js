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
            getArticle: function (id) {
                return $http.get('/api/article/' + id);
            },
            getPersons: function () {
                return $http.get('/api/person/');
            },
            updateObject: function (obj) {
                // When the server receives this object,
                // it will overwrite the server data for each listed field and leave the others unchanged
                return {
                    "object": obj,
                    "updated_fields": []
                };
            },
            updateArticle: function (updates, scope, success) {
                $http.put('/api/article/update', updates)
                    .success(function (data) {
                        angular.forEach(updates.updated_fields, function (field) {
                            scope.original[field] = angular.copy(data[field]);
                        });
                        success();
                    }
                )
                    .error(function () {
                        alert("Error!");
                    }
                );
            },
            changed: function (object, scope) {
                return !angular.equals(scope.article[object], scope.original[object]);
            },
            revert: function (object, scope) {
                scope.article[object] = angular.copy(scope.original[object]);
            }
        }
    });