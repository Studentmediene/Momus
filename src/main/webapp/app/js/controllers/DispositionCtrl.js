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
    .controller('DispositionCtrl', function ($scope, $routeParams, ArticleService, PublicationService, MessageModal, $location, $modal) {
        $scope.pubId = $routeParams.id;
        $scope.loading = 5;
        $scope.newPageAt = 0;
        $scope.numNewPages = 1;

        if($scope.pubId){
            PublicationService.getById($scope.pubId).success(function(data) {
                if(!data){
                    $location.path("/");
                    return;
                }
                $scope.publication = data;
                $scope.getPages();
            });
        } else{
            PublicationService.getAll().success(function(data){
                $scope.publication = PublicationService.getActive(data);
                $scope.getPages();
            });
        }

        $scope.getPages = function(){
            var pubId = $scope.publication.id;
            ArticleService.search({publication: pubId}).success(function (data) {
                $scope.publication.articles = data;
                $scope.loading--;
            });
            PublicationService.getPages(pubId).success(function (data){
                $scope.publication.pages = data;
                $scope.loading--;
                console.log($scope.publication);
            });
            ArticleService.getReviews().success(function(data){
                $scope.reviewOptions = data;
                $scope.loading--;
            });

            ArticleService.getStatuses().success(function(data){
                $scope.statusOptions = data;
                $scope.loading--;
            });

            PublicationService.getLayoutStatuses().success(function(data){
                $scope.layoutStatuses = data;
                $scope.loading--;
            });
        };

        $scope.showHelp = function(){
            MessageModal.info("<p>Genererer en disposisjon etter beste evne. Bruker artiklene i den gjeldende utgaven til å " +
                "generere en standarddisposisjon");
        };

        $scope.generateDispBtn = function(){
            if($scope.publication.pages.length == 0){
                $scope.generateDisp();
            } else if(confirm("Dette vil overskrive den nåværende disposisjonen")){
                $scope.generateDisp();
            }
        };

        /*
        *   Not used at the moment, left here in case it's wanted later
        */
        $scope.generateDisp = function(){
            PublicationService.generateDisp($scope.publication.id).success(function(data){
                $scope.publication.pages = data;
            })
        };

        $scope.savePublication = function() {
            PublicationService.updateMetadata($scope.publication);
        };

        $scope.deletePage = function(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                PublicationService.deletePage(page.id).success(function(){
                    PublicationService.getPages($scope.publication.id).success(function(data){
                        $scope.publication.pages = data;
                        sortPages();
                        $scope.savePublication();
                    });
                });
            }
        };

        $scope.newPage = function(){
            var insertPageAt = $scope.newPageAt;
            var numNewPages = $scope.numNewPages;
            if(numNewPages>100){
                numNewPages = 100;
            }else if(numNewPages <= 0){
                numNewPages = 1;
            }
            for(var i = 0; i < numNewPages; i++) {
                var temp_page = {
                    page_nr: $scope.publication.pages.length + 1,
                    note: null,
                    advertisement: false,
                    articles: [],
                    publication: $scope.publication.id,
                    layout_status: $scope.getLayoutStatusByName("Ukjent")
                };
                    if (0 <= insertPageAt && insertPageAt <= $scope.publication.pages.length) {
                        $scope.publication.pages.splice(insertPageAt, 0, temp_page);
                    } else {
                        $scope.publication.pages.push(data);
                    }
                    sortPages();
            }
            $scope.savePublication();
        };

        function sortPages(){
            for ( var i = 0; i < $scope.publication.pages.length; i++ ) {
                $scope.publication.pages[i].page_nr = i+1;
            }
        }

        $scope.savePage = function() {
            PublicationService.updateMetadata($scope.publication);
        };

        $scope.getLayoutStatusByName = function(name){
            for(var i = 0; i < $scope.layoutStatuses.length; i++){
                if($scope.layoutStatuses[i].name == name){
                    return $scope.layoutStatuses[i];
                }
            }
            return null;
        };

        $scope.createArticle = function(page){
            var modal = $modal.open({
                templateUrl: 'partials/article/createArticleModal.html',
                controller: 'CreateArticleModalCtrl',
                resolve: {
                    pubId: function(){
                        return $scope.publication.id;
                    }
                }
            });
            modal.result.then(function(id){
                ArticleService.getArticle(id).success(function(data){
                    page.articles.push(data);
                    $scope.publication.articles.push(data);
                    $scope.savePage();
                })
            })

        };

        $scope.saveArticle = function(article){
            ArticleService.updateMetadata(article);
        };

        $scope.sortableOptions = {
            helper: function(e, ui) {
                ui.children().each(function () {
                    $(this).width($(this).width());
                });
                return ui;
            },
            axis: 'y',
            handle: '.handle',
            stop: function(e, ui){
                $scope.selectedPage = $scope.publication.pages[ui.item.index()];
                sortPages();
                PublicationService.updateMetadata($scope.publication);
            }
        }
    });
