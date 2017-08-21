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
    .controller('DispositionCtrl', function ($scope, $routeParams, ArticleService, PublicationService, MessageModal, $location, $modal, $templateRequest, $route, $window,uiSortableMultiSelectionMethods, $q, Publication, Page) {
        var vm = this;

        vm.maxNewPages = 100;

        vm.newPage = newPage;
        vm.updatePage = updatePage;
        vm.deletePage = deletePage;

        vm.createArticle = createArticle;
        vm.saveArticle = saveArticle;

        vm.showHelp = showHelp;

        // Style
        var pageDoneColor = "#DDFFCB";
        var pageAdColor = "#f8f8f8";
        vm.pageColor = function(page) {
            return (page.done ? pageDoneColor : (page.advertisement ? pageAdColor : '#FFF'));
        };

        // Widths of columns in the disp. Used to sync widths across pages (which are separate tables)
        vm.responsiveColumns = {
            name: {minWidth: '100px'},
            journalist: {minWidth: '80px'},
            photographer: {minWidth: '80px'},
            pstatus: {minWidth: '90px'},
            comment: {minWidth: '100px'}
        };
        vm.dispSortableStyle = {}; // Used to prevent the disposition from breaking while moving pages

        // Get all data
        getStatuses();
        getDisposition();

        function getDisposition(){
            vm.loading = true;
            var pubid = $routeParams.id;
            getPublication($routeParams.id, function(publication){
                getPages(publication.id, function(pages){
                    publication.pages = pages;
                    ArticleService.search({publication: publication.id}).then(function(data){
                        vm.articles = data.data;
                        vm.publication = publication;
                        vm.loading = false;
                    });
                });
            });
        }

        function getPages(pubid, callback){
            var pages = Page.query({pubid: pubid}, function(){ callback(pages);});
        }

        function getPublication(pubid, callback){
            var publication;
            if(pubid){
                publication = Publication.get({id: pubid}, function(){ callback(publication);});
            }else{
                publication = Publication.active(callback, function(){ callback(publication);});
            }
        }

        function getStatuses(){
            $q.all([ArticleService.getReviews(), ArticleService.getStatuses()]).then(function(data){
                vm.reviewStatuses = data[0].data;
                vm.articleStatuses = data[1].data;
            });
            vm.layoutStatuses = Publication.layoutStatuses();
        }

        function newPage(newPageAt, numNewPages){
            for(var i = 0; i < numNewPages; i++) {
                var page = {pageNr: newPageAt + i + 1, publicationid: vm.publication.id, layoutStatus: getLayoutStatusByName("Ukjent")};
                var addedPage = Page.save({pubid: vm.publication.id}, page, function() {
                    vm.publication.pages.splice(newPageAt, 0, addedPage);
                });
            }
        }

        function updatePage(page) {
            var pages = Page.update({pubid: vm.publication.id}, page, function() {
                vm.publication.pages = pages;
            });
        }

        function deletePage(page) {
            if(confirm("Er du sikker på at du vil slette denne siden?")){
                vm.publication.pages.splice(vm.publication.pages.indexOf(page), 1);
                page.$delete({pubid: vm.publication.id, pageid: page.id});
            }
        }

        function createArticle(page){
            var modal = $modal.open({
                templateUrl: 'partials/article/createArticleModal.html',
                controller: 'CreateArticleModalCtrl',
                resolve: {
                    pubId: function(){
                        return vm.publication.id;
                    }
                }
            });
            modal.result.then(function(id){
                ArticleService.getArticle(id).success(function(data){
                    page.articles.push(data);
                    vm.publication.articles.push(data);
                    updatePage(page);
                });
            });
        }

        function saveArticle(article){
            ArticleService.updateMetadata(article);
        }

        function showHelp(){
            $templateRequest('partials/templates/help/dispHelp.html').then(function(template){
                MessageModal.info(template);
            });
        }

        //TODO put this in a service
        function getLayoutStatusByName(name){
            for(var i = 0; i < vm.layoutStatuses.length; i++){
                if(vm.layoutStatuses[i].name === name){
                    return vm.layoutStatuses[i];
                }
            }
            return null;
        }

        vm.sortableOptions = uiSortableMultiSelectionMethods.extendOptions({
            helper: uiSortableMultiSelectionMethods.helper,
            start: function(e, ui) {
                vm.dispSortableStyle.tableLayout = "auto"; //To not break the table
                $scope.$apply(); //Apply since this is jQuery stuff

                //Calculate height of placeholder
                var totalHeight = 0;
                for(var i = 0; i< ui.helper[0].children.length;i++){
                    var child = ui.helper[0].children[i];
                    totalHeight += parseInt($window.getComputedStyle(child).height.replace("px", ""));
                }
                ui.placeholder[0].style.height = totalHeight +"px";
            },
            axis: 'y',
            handle: '.handle',
            stop: function(e, ui){
                vm.dispSortableStyle.tableLayout = "";
                var placed = ui.item.sortableMultiSelect.selectedModels;
                var newPosition = vm.publication.pages.indexOf(placed[0]);
                placed[0].page_nr = newPosition + 1
                updatePage(placed[0]);
            },
            placeholder: "disp-placeholder"
        });

        function updateDispSize(){
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
                    width = parseInt(vm.responsiveColumns[k].minWidth);
                }

                vm.responsiveColumns[k] = {minWidth: width + 'px', maxWidth: width + 'px', width: width + 'px'};
            }
        }

        updateDispSize();
        // To recalculate disp table when resizing the screen
        angular.element($window).bind('resize', function(){
            updateDispSize();
            $scope.$apply();
        });

        $scope.$on('$destroy', function(){
            angular.element($window).unbind('resize');
        });
    });
