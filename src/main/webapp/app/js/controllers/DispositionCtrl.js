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
    .controller('DispositionCtrl', function ($scope, $routeParams, ArticleService, PublicationService, MessageModal, $location, $modal, $templateRequest, $route, $window,uiSortableMultiSelectionMethods, $q) {
        var vm = this;
        vm.maxNewPages = 100;
        vm.testing = testing;

        $scope.pageDoneColor = "#DDFFCB";
        $scope.pageAdColor = "#f8f8f8";

        // Widths of columns in the disp. Uses ngStyle to sync widths across pages (which are separate tables)
        // The widths that are here will be used when the app is loaded on a screen so small the disp gets a scroll bar
        $scope.responsiveColumns = {
            comment: {minWidth: '100px'},
            name: {minWidth: '100px'},
            journalist: {minWidth: '80px'},
            photographer: {minWidth: '80px'},
            pstatus: {minWidth: '90px'}
        };
        vm.dispSortableStyle = {}; // Used to prevent the disposition from breaking while moving pages

        function testing(){
            console.log("submitted");
        }

        function getPublication(pubid){
            if(pubid){
                return PublicationService.getById(pubid).success(function(data){
                    return data;
                });
            }else{
                return PublicationService.getActive().then(function(data){
                    return data.data;
                });
            }
        }

        function getPages(pubid){
            return getPublication(pubid).then(function(publication) {
                pubid = publication.id;
                return ArticleService.search({publication: pubid}).then(function(data){
                    publication.articles = data.data;
                    return PublicationService.getPages(pubid).then(function(data){
                        publication.pages = data.data;
                        return publication;
                    })
                })
            })
        }

        function getDisposition(){
            vm.loading = true;
            var pubid = $routeParams.id;
            return getPages(pubid).then(function(publication){
                PublicationService.linkPagesToArticles(publication.pages, publication.articles);
                vm.publication = publication;
                vm.loading = false;
            });
        }

        function getStatuses(){
            return $q.all([ArticleService.getReviews(), ArticleService.getStatuses(), PublicationService.getLayoutStatuses()]).then(function(data){
                vm.reviewStatuses = data[0].data;
                vm.articleStatuses = data[1].data;
                vm.layoutStatuses = data[2].data;
            });
        }

        getStatuses();
        getDisposition();

        $scope.savePublication = function() {
            PublicationService.updateMetadata($scope.publication).success(function(data){
                $scope.getPages();
            });
        };

        $scope.newPage = function(){
            var insertPageAt = vm.newPageAt;
            var numNewPages = vm.numNewPages;
            if(numNewPages>100){
                numNewPages = 100;
            }else if(numNewPages <= 0){
                numNewPages = 1;
            }
            // Backend?
            for(var i = 0; i < numNewPages; i++) {
                var page = {
                    page_nr: $scope.publication.pages.length + 1,
                    note: "",
                    advertisement: false,
                    articles: [],
                    publication: $scope.publication.id,
                    layout_status: $scope.getLayoutStatusByName("Ukjent")
                };
                    if (0 <= insertPageAt && insertPageAt <= $scope.publication.pages.length) {
                        $scope.publication.pages.splice(insertPageAt, 0, page);
                    } else {
                        $scope.publication.pages.push(data);
                    }
                    sortPages();
            }
            $scope.savePublication();
        };

        function sortPages(){
            for ( var i = 0; i < vm.publication.pages.length; i++ ) {
                vm.publication.pages[i].page_nr = i+1;
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

        // Colorize page based on its status (css style)
        $scope.pageColor = function(page){
            return 'background: ' + (page.done ? $scope.pageDoneColor : (page.advertisement ? $scope.pageAdColor : '#FFF'));
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
            helper: uiSortableMultiSelectionMethods.helper,
            start: function(e, ui) {
                vm.dispSortableStyle.tableLayout = "auto"; //To not break the table
                $scope.$apply(); //Apply since this is jQuery stuff
                var totalHeight = 0;
                for(var i = 0; i< ui.helper[0].children.length;i++){
                    var child = ui.helper[0].children[i];
                    totalHeight += parseInt($window.getComputedStyle(child)['height'].replace("px", ""));
                }
                ui.placeholder[0].style.height = totalHeight +"px";
            },
            axis: 'y',
            handle: '.handle',
            stop: function(e, ui){
                $scope.selectedPage = vm.publication.pages[ui.item.index()];
                //PublicationService.updateMetadata($scope.publication);
                vm.dispSortableStyle.tableLayout = "";
                $scope.updateDispSize(); //In order to put the tableLayout back to the correct value
            },
            placeholder: "disp-placeholder"
        });

        $scope.generateColophon = function(){
            PublicationService.getColophon($scope.publication.id);
        };

        $scope.showHelp = function(){
            $templateRequest('partials/templates/help/dispHelp.html').then(function(template){
                MessageModal.info(template);
            });
        };

        $scope.updateDispSize = function(){
            var constantArticleSize = 320;
            var constantDispSize = constantArticleSize + 220;
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
                }else{ //Use min width and scroll if screen is too small.
                    width = parseInt($scope.responsiveColumns[k].minWidth);
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
