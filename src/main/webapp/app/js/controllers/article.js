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

angular.module('momusApp.controllers')
    .controller('ArticleCtrl', function ($scope, $http, $routeParams, articleParserRules, noteParserRules) {

//        $http.get('/api/article').success(function (data) {
//            $scope.article = data;
//            $scope.originalArticle = angular.copy($scope.article);
//        });

        // This is am array of dummy articles, in the future we will GET the url-specified article the proper way
        var articles = [
            {
                content: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin aliquam lorem ullamcorper placerat elementum. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam non viverra nulla. Etiam ante nunc, elementum eget porta quis, blandit vel nisi. Donec ultricies tortor at urna viverra, ut rhoncus erat fringilla. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Nam a scelerisque justo. Quisque ut semper odio. Duis semper ut enim sed blandit. Mauris at vulputate arcu. Etiam tortor est, tincidunt condimentum ornare eget, tristique vel lectus. Vivamus dignissim tempor arcu id tempus. Maecenas sapien tortor, viverra in pretium ac, pulvinar sed lacus. Etiam consequat, libero ut porta malesuada, metus ante tristique quam, sed suscipit massa eros vitae justo. Vivamus ac tortor ac lacus venenatis feugiat. Suspendisse tempor dapibus urna, ac dictum ipsum fermentum in.",
                note: {
                    content: "Hei hå"
                },
                journalists: [
                    { name: "Mats" },
                    { name: "Mathias" }
                ],
                photographers: [
                    { name: "Bob" },
                    { name: "Bård" }
                ]
            },
            {
                content: "If something like that isn't possible you can change your templateUrl to point to a partial html file that just has ng-include and then set the url in your controller using $routeParams like this:",
                note: {
                    content: "nå er det jul igjen"
                },
                journalists: [
                    { name: "Mats" },
                    { name: "Bård" }
                ],
                photographers: [
                    { name: "Steve" },
                    { name: "Mathias" }
                ]
            }
        ];

        $scope.article = articles[$routeParams.id];
        $scope.originalArticle = angular.copy($scope.article);

        $scope.articleRules = articleParserRules;

        $scope.$watch('article.content', function (newVal, oldVal) {
            $scope.articleIsDirty = !angular.equals($scope.article, $scope.originalArticle);
        });

        $scope.saveArticle = function () {
            $scope.originalArticle = angular.copy($scope.article);
            $scope.articleIsDirty = false;
//            $http.put('/api/article', $scope.article).success(function (data) {
//                $scope.article = data;
//                $scope.originalArticle = angular.copy($scope.article);
//                $scope.articleIsDirty = false;
//            })
        };

        // noteCtrl
//        $http.get('/api/note').success(function (data) {
//            $scope.note = data;
//            $scope.originalNote = angular.copy($scope.note);
//        });

        $scope.note = $scope.article.note;

        $scope.noteRules = noteParserRules;

        $scope.$watch('note.content', function (newVal, oldVal) {
            $scope.noteIsDirty = !angular.equals($scope.note, $scope.originalNote);
        });

        $scope.saveNote = function () {
            $http.put('/api/note', $scope.note).success(function (data) {
                $scope.note = data;
                $scope.originalNote = angular.copy($scope.note);
                $scope.noteIsDirty = false;
            })
        };

    });

