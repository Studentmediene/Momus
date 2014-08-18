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

angular.module('momusApp.controllers')
    .controller('DispositionCtrl', function ($scope, $http, $routeParams, $modal) {
        $scope.addArticle = function () {
            console.log("TRYKKER PÅ NOE");
//            $scope.disposition.pages[page.pageNr-1].articles.push;
        };

        //id, name, content, note, contentLength, status, type, publication,
        // journalists, photographers, correctResponsible, lastUpdated
        $http.get('/api/article').success(function (data) {
            $scope.articles = data;
            console.log(data + "Articles hentet");
        });

        function setDisposition(data) {
            if (!data.pages) {
                data.pages = [];
            }
            console.log("disp data: " + data);
            $scope.disposition = data;
        }

        // Get sections from database
        $http.get('/api/disp/section').success(function (data) {
            $scope.sections = data;
        });

        //Get Disposition
        $http.get('/api/disp/' + $routeParams.id).success(setDisposition);

        //Update disp
        function saveArticle() {
            $http.put('/api/disp/' + $routeParams.id, $scope.disposition).success(setDisposition);
        }


        $scope.saveArticle = saveArticle;
        $scope.addPage = function () {

            var newPage = {
                page_nr: $scope.disposition.pages.length + 1,
                section: $scope.sections[0],
                note: "",
                articles: []
            };
            $scope.disposition.pages.push(newPage);

            saveArticle();
        };

        $scope.removeLastPage = function () {
            var pages = $scope.disposition.pages;

            if (!pages || pages.length < 1) {
                return;
            }
            for ( var i = 0;  i < pages.length; i++ )
            {
                if(pages[i].page_nr == pages.length){
                    pages.splice(i,1);
                    saveArticle();
                    return
                }
            }
        }

        $scope.removePage = function (page) {
            var pages = $scope.disposition.pages;
            var k = -1;

            for (var i = 0; i < pages.length; i++){
                if( pages[i].page_nr == page.page_nr){
                    k = i;
                }
            }
            if( k < 0){
                return;
            }
            // If the page is empty (no article), remove it.
            if (pages[k].articles.length === 0 || confirm("Slette denne siden?")) {
                pages.splice(k,1);

                for (var i = 0; i < pages.length; i++){

                    if( pages[i].page_nr > page.page_nr){
                        pages[i].page_nr -= 1;
                    }
                }
                saveArticle();
            }


        };



//        $scope.articles = [
//            {
//                id: 1,
//                type: "KulturRaport",
//                name: "Fuglefrø",
//                status: "Skrives",
//                photoStatus: "tatt",
//                advertisement: false,
//                photographers: [
//                    {
//                        name: "jon",
//                        age: 24
//                    },
//                    {
//                        name: "birger",
//                        age: 22
//                    },
//                    {
//                        name: "olav",
//                        age: 45
//                    },
//                    {
//                        name: "kåre",
//                        age: 45
//                    }
//                ],
//                journalists: [
//                    {
//                        name: "håkon",
//                        age: 24
//                    },
//                    {
//                        name: "bård",
//                        age: 22
//                    },
//                    {
//                        name: "stian",
//                        age: 45
//                    }
//                ]
//            },
//            {
//                id: 2,
//                type: "Portrett",
//                name: "Nato Jens",
//                status: "Desk",
//                photoStatus: "lagt inn",
//                advertisement: true,
//                photographers: [],
//                journalists: [
//                    {
//                        name: "ole",
//                        age: 20
//                    }
//                ]
//            },
//            {
//                id: 3,
//                type: "Miljø",
//                name: "Oljesøl",
//                status: "Skrives",
//                photoStatus: "redigeres",
//                advertisement: false,
//                photographers: [],
//                journalists: [
//                    {
//                        name: "frode",
//                        age: 28
//                    }
//                ]
//            }
//
//        ];


//        $scope.sections = [
//            {id: 1, name: "FORSIDE"},
//            {id: 2, name: "INNHOLD"},
//            {id: 3, name: "ANNONSE"},
//            {id: 4, name: "NYHET"},
//            {id: 5, name: "TRANSIT"},
//            {id: 6, name: "FORSKNINGSFUNN"},
//            {id: 7, name: "DAGSORDEN"},
//            {id: 8, name: "MENINGER"},
//            {id: 9, name: "AKTUALITET"},
//            {id: 10, name: "SMÅREP"},
//            {id: 11, name: "KULTUR"},
//            {id: 12, name: "SPIT"},
//            {id: 13, name: "BAKSIDE"},
//            {id: 14, name: "TEST"}
//        ];

        // Test disposition
//        $scope.disposition = {
//            id: $routeParams.id,
//            publicationNr: 3,
//            release_date: 2014,
//            pages: [
//                {
//                    pageNr: 1,
//                    section: {id: 0, name: ""},
//                    note:
//                    articles: [
//                        {
//                            id: 1,
//                            type: "KulturRaport",
//                            name: "Fuglefrø",
//                            status: "Skrives",
//                            photoStatus: "tatt",
//                            advertisement: false,
//                            photographers: [
//                                {
//                                    name: "jon",
//                                    age: 24
//                                },
//                                {
//                                    name: "birger",
//                                    age: 22
//                                },
//                                {
//                                    name: "olav",
//                                    age: 45
//                                },
//                                {
//                                    name: "kåre",
//                                    age: 45
//                                }
//                            ],
//                            journalists: [
//                                {
//                                    name: "håkon",
//                                    age: 24
//                                },
//                                {
//                                    name: "bård",
//                                    age: 22
//                                },
//                                {
//                                    name: "stian",
//                                    age: 45
//                                }
//                            ]
//                        },
//                        {
//                            id: 2,
//                            type: "Portrett",
//                            name: "Nato Jens",
//                            status: "Desk",
//                            photoStatus: "lagt inn",
//                            advertisement: true,
//                            photographers: [],
//                            journalists: [
//                                {
//                                    name: "ole",
//                                    age: 20
//                                }
//                            ]
//                        }
//                    ]
//                },
//                {
//                    pageNr: 2,
//                    section: {id: 0, name: ""},
//                    articles: [
//                        {
//                            id: 3,
//                            type: "Miljø",
//                            name: "Oljesøl",
//                            status: "Skrives",
//                            photoStatus: "redigeres",
//                            advertisement: false,
//                            photographers: [],
//                            journalists: [
//                                {
//                                    name: "frode",
//                                    age: 28
//                                }
//                            ]
//                        }
//                    ]
//                },
//                {
//                    pageNr: 3,
//                    section: {id: 0, name: ""},
//                    articles: [
//
//                    ]
//                },
//                {
//                    pageNr: 4,
//                    section: {id: 0, name: ""},
//                    articles: [
//
//                    ]
//                }
//            ]
//        };


        var ModalInstanceCtrl = function ($scope, $modalInstance, articles, page) {


            $scope.selectedArticles = { };
            $scope.page = page;
            $scope.articles = articles;

            if (articles.length !== 0) {
                $scope.selectedArticles.addArticleModel = articles[0];
            }
            // if the page.articles is not empty, set a default value for the model
            if ($scope.page.articles.length !== 0) {
                $scope.selectedArticles.delArticleModel = $scope.page.articles[0];
            }

            $scope.addArticle = function (articleModel) {
                if (!articleModel) {
                    return;
                }
                var pageAricles = $scope.page.articles;
                for (var i = 0; i < pageAricles.length; i++) {
                    if (pageAricles[i].id == articleModel.id) {
                        return;
                    }
                }
                $scope.page.articles.push(articleModel);
                //To make adding the first article look good: puts a default
                $scope.selectedArticles.delArticleModel = $scope.page.articles[0];

            };

            $scope.removeArticle = function (articleModel) {
                if (articleModel) {
                    var pageAricles = $scope.page.articles;
                    for (var i = 0; i < pageAricles.length; i++) {
                        if (pageAricles[i].id == articleModel.id) {
                            $scope.page.articles.splice(i, 1);
                            //To make removal look good: re-checking list to put a new default
                            if ($scope.page.articles.length != 0) {
                                console.log("Noe", $scope.page.articles[0]);
                                $scope.selectedArticles.delArticleModel = $scope.page.articles[0];
                            }
                            return;
                        }
                    }

                }
            };


        };

        $scope.articleModal = function (page) {

            var modalInstance = $modal.open({
                templateUrl: 'partials/disposition/articleModal.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    articles: function () {
                        return $scope.articles;
                    },
                    page: function () {
                        return page;
                    }
                }

            });

        };
    });