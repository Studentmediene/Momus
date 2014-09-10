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
        var pages;

        $http.get('/api/article').success(function (data) {
            $scope.articles = data;
            console.log(data + "Articles hentet");

            //Get Disposition
            $http.get('/api/disp/' + $routeParams.id).success( setDisposition);

        });

        //id, name, content, note, contentLength, status, type, publication,
        // journalists, photographers, correctResponsible, lastUpdated


        // Checks every article in the disposition and replaces them with
        // articles from the database list.
        // This makes all articles the same Objects
        function setDisposition(data) {
            var art = $scope.articles;
            if (!data.pages) {
                data.pages = [];
            }
            data.pages.forEach( function (page) {
               page.articles = page.articles.map(function(article){
                for ( var i = 0; i < art.length; i++){
                    if ( article.id === art[i].id ){
                        return art[i];
                    }
                }
                   return null;
               });
            });

            data.pages.sort( function ( a, b ) {
                return a.page_nr - b.page_nr;
            });
            console.log("disp data: " + data);
            $scope.disposition = data;
            pages = $scope.disposition.pages;
            if ($scope.selectedPage){
                $scope.selectedPage = pages[$scope.selectedPage.page_nr-1];
            }
        }

        // Get sections from database
        $http.get('/api/disp/section').success(function (data) {
            $scope.sections = data;
        });

        //Update disp
        function saveArticle() {
            $http.put('/api/disp/' + $routeParams.id, $scope.disposition).success(setDisposition);
        }

        function SortPages(){
            for ( var i = 0; i < pages.length; i++ ) {
                pages[i].page_nr = i+1;
            }
        }

        $scope.saveArticle = saveArticle;
        $scope.addLastPage = function () {

            var newPage = {
                page_nr: pages.length + 1,
                section: $scope.sections[0],
                note: "",
                articles: []
            };
            pages.push(newPage);

//            saveArticle();
        };

        $scope.addPage = function(page){
            var pageIndex = pages.indexOf(page);
            var newPage = {
                page_nr: pageIndex +1,
                section: $scope.sections[0],
                note: "",
                articles: []
            };
            pages.splice(pageIndex,0,newPage);
            pageIndex = pages.indexOf(page);
            SortPages();
//            saveArticle();
        };

        $scope.removeLastPage = function () {

            if (!pages || pages.length < 1) {
                return;
            }

                for ( var i = 0; i < pages.length; i++) {
                    if (pages[i].page_nr == pages.length) {
                        pages.splice(i, 1);
//                        saveArticle();
                        return
                    }
                }
        };

        $scope.removePage = function (page) {
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
//                saveArticle();
            }
            $scope.selectedPage = pages[k];

        };

        $scope.photostatus = [
            "Uferdig","Planlagt","Tatt"
        ];

        // When a node is dropped we uppdate the page number/index+1
        $scope.treeOptions = {
            dropped: function(event) {
                $scope.selectedPage = pages[event.dest.index];
                SortPages();

            }
        };
        $scope.articleModal = function (page , article) {

            var modalInstance = $modal.open({
                templateUrl: 'partials/disposition/articleModal.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    articles: function () {
                        return $scope.articles;
                    },
                    page: function () {
                        return page;
                    },
                    article: function () {
                        return article;
                    }
                }

            });

        };

        var ModalInstanceCtrl = function ($scope, $modalInstance, articles, page, article) {


            $scope.selectedArticles = { };
            $scope.page = page;
            $scope.articles = articles;
            $scope.article = article;

            if (articles.length !== 0) {
                $scope.selectedArticles.addArticleModel = articles[0];
            }
            // if the page.articles is not empty, set a default value for the model
            if ($scope.page.articles.length !== 0) {
                if ($scope.article){
                    $scope.selectedArticles.delArticleModel = $scope.article;
                }
                else{
                    $scope.selectedArticles.delArticleModel = $scope.page.articles[0];
                }
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
                $scope.selectedArticles.delArticleModel = articleModel;

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


    });
