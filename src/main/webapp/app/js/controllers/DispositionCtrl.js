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

angular.module('momusApp.controllers')
    .controller('DispositionCtrl', function ($scope, $routeParams, ArticleService, PublicationService, MessageModal, $location, $modal, $templateRequest, $route, $window,uiSortableMultiSelectionMethods) {
        $scope.pubId = $routeParams.id;
        $scope.loading = 5;
        $scope.newPageAt = 0;
        $scope.numNewPages = 1;
        $scope.editPhotoStatus = false;

        // Widths of columns in the disp. Uses ngStyle to sync widths across pages (which are separate tables)
        // The widths that are here will be used when the app is loaded on a screen so small the disp gets a scroll bar
        $scope.responsiveColumns = {
            comment: {minWidth: '100px'},
            name: {minWidth: '100px'},
            journalist: {minWidth: '80px'},
            photographer: {minWidth: '80px'},
            pstatus: {minWidth: '90px'}
        };

        //ngStyle for disp table layout.
        //Should be auto when screen is smaller than 992 px and fixed when above
        $scope.dispTableLayout = {
            tableLayout: "auto"
        };

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
            PublicationService.getActive().success(function(data){
                $scope.publication = data;
                $scope.getPages();
            });
        }

        $scope.getPages = function(){
            var pubId = $scope.publication.id;
            ArticleService.search({publication: pubId}).success(function (data) {
                $scope.publication.articles = data;
                $scope.loading--;
                PublicationService.getPages(pubId).success(function (data){
                    $scope.publication.pages = data;
                    PublicationService.linkPagesToArticles($scope.publication.pages, $scope.publication.articles);
                    $scope.loading--;
                });
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

        /*
        *   Not used at the moment, left here in case it's wanted later
        */
        $scope.generateDisp = function(){
            PublicationService.generateDisp($scope.publication.id).success(function(data){
                $scope.publication.pages = data;
            })
        };

        $scope.savePublication = function() {
            PublicationService.updateMetadata($scope.publication).success(function(data){
                $scope.getPages();
            });
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

        //TODO put this in a service
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

        $scope.sortableOptions = uiSortableMultiSelectionMethods.extendOptions({
            //helper: function(e, ui) {
            //    var c = ui.clone();
            //    c.addClass("disp-helper");
            //    $scope.dispTableLayout.tableLayout = "auto"; //To not break the table
            //    $scope.$apply(); //Apply since this is jQuery stuff
            //    return c;
            //},
            helper: uiSortableMultiSelectionMethods.helper,
            axis: 'y',
            handle: '.handle',
            stop: function(e, ui){
                $scope.selectedPage = $scope.publication.pages[ui.item.index()];
                sortPages();
                PublicationService.updateMetadata($scope.publication);
                $scope.updateDispSize(); //In order to put the tableLayout back to the correct value
            },
            placeholder: "disp-placeholder",
            'ui-selection-count':true
        });

        $scope.showHelp = function(){
            $templateRequest('partials/templates/help/dispHelp.html').then(function(template){
                MessageModal.info(template);
            });
        };

        $scope.updateDispSize = function(){
            var constantArticleSize = 320;
            var constantDispSize = constantArticleSize + 190;
            var dispWidth = angular.element(document.getElementById("disposition")).context.clientWidth;

            //Must divide rest of width between journalists, photographers, photo status and comment.
            var widthLeft = dispWidth - constantDispSize;
            var shareParts = {
                name: 0.25,
                journalist: 0.2,
                photographer: 0.2,
                pstatus: 0.15,
                comment: 0.2
            };

            for(var k in shareParts){
                var width;
                if($window.innerWidth > 992){
                    width = Math.floor(shareParts[k]*widthLeft);
                    $scope.dispTableLayout.tableLayout = "fixed";
                }else{ //Use min width and scroll if screen is too small.
                    width = parseInt($scope.responsiveColumns[k].minWidth);
                    $scope.dispTableLayout.tableLayout = "auto";
                }

                $scope.responsiveColumns[k] = {minWidth: width + 'px', maxWidth: width + 'px', width: width + 'px'};
            }
        };

        angular.element($window).bind('resize', function(){
            $scope.updateDispSize();
            $scope.$apply();
        });

        $scope.updateDispSize();
    });
