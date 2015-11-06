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
    .controller('DispositionCtrl', function ($scope, $routeParams, ArticleService, PublicationService, MessageModal) {
        PublicationService.getLayoutStatuses().success(function(data){
            $scope.layoutStatuses = data;
        });
        $scope.pubId = $routeParams.id;

        if($scope.pubId){
            PublicationService.getById($scope.pubId).success(function(data) {
                $scope.publication = data;

                $scope.getPages($scope.publication.id);
            });
        } else {
            PublicationService.getAll().success(function(data){
                $scope.publication = PublicationService.getActive(data);
                $scope.getPages($scope.publication.id);
            });
        }

        $scope.getPages = function(pubId){
            ArticleService.search({publication: pubId}).success(function (data) {
                $scope.publication.articles = data;
            });
            PublicationService.getPages(pubId).success(function (data){
                $scope.publication.pages = data;
            });
            ArticleService.getReviews().success(function(data){
                $scope.reviewOptions = data;
            });

            ArticleService.getStatuses().success(function(data){
                $scope.statusOptions = data;
            });
        };

        $scope.showHelp = function(){
            MessageModal.info("<p>Genererer en disposisjon etter beste evne. Bruker artiklene i den gjeldende utgaven til å " +
                "generere en standarddisposisjon");
        };

        $scope.generateDispBtn = function(){
            //if(confirm("Dette vil overskrive den nåværende disposisjonen")){
                $scope.generateDisp();
            //}
        };

        $scope.generateDisp = function(){
            PublicationService.generateDisp($scope.publication.id).success(function(data){
                $scope.publication.pages = data;
            })
        };

        $scope.generatePages = function(){
            $scope.publication.pages = [];
            for(var i = 0; i < $scope.publication.articles.length; i++){

                var temp_page = {
                    page_nr : i+1,
                    note : "",
                    advertisement: false,
                    articles: [$scope.publication.articles[i]],
                    publication: $scope.publication.id,
                    layout_status: $scope.getLayoutStatusByName("Ukjent")
                };

                $scope.publication.pages.push(temp_page);
            }
            PublicationService.updateMetadata($scope.publication);

        };


        $scope.savePage = function() {
            PublicationService.updateMetadata($scope.publication);
        };

        $scope.newPage = function(){
            var temp_page = {
                page_nr : $scope.publication.pages.length + 1,
                note : null,
                advertisement: false,
                articles: [],
                publication: $scope.publication.id,
                layout_status: $scope.getLayoutStatusByName("Ukjent")
            };
            //$scope.publication.pages.push(temp_page);
            PublicationService.createPage(temp_page).success(function(data){
                $scope.publication.pages.push(data);
            });
            //PublicationService.updateMetadata($scope.publication);
        };

        $scope.saveArticle = function(article){
            ArticleService.updateMetadata(article);
        };

        // Drag&drop stuff
        $scope.treeOptions = {
            dropped: function(event){
                $scope.selectedPage = $scope.publication.pages[event.dest.index];
                sortPages();
                PublicationService.updateMetadata($scope.publication);
            }
        };

        function sortPages(){
            for ( var i = 0; i < $scope.publication.pages.length; i++ ) {
                $scope.publication.pages[i].page_nr = i+1;
            }
        }

        $scope.getLayoutStatusByName = function(name){
            for(var i = 0; i < $scope.layoutStatuses.length; i++){
                if($scope.layoutStatuses[i].name == name){
                    return $scope.layoutStatuses[i];
                }
            }
            return null;
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
