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
    .controller('FrontPageCtrl', function ($scope, $q, NoteService, noteParserRules, PersonService, ArticleService, TipAndNewsService, ViewArticleService, FavouriteSectionService, PublicationService, $location) {

        $scope.randomTip = function() {
            $scope.tip = TipAndNewsService.getRandomTip();
        };

        $scope.randomTip();

        $scope.news = TipAndNewsService.getNews();


        // Latest user articles

        $scope.loadingArticles = true;
        PersonService.getCurrentUser().success(function(user){
            $scope.user = user;
            ArticleService.search({persons: [user.id], page_size: 9}).success(function (articles) {
                $scope.loadingArticles = false;
                $scope.myArticles = articles;
                if($scope.myArticles.length <= 0 ){
                    $scope.noArticles = true;
                }
            });
        });


        // Recently viewed articles

        $scope.recentArticles = ViewArticleService.getRecentViews();
        if($scope.recentArticles){
            $scope.loadingRecent = true;
            ArticleService.getMultiple($scope.recentArticles).success(function(data){
                $scope.loadingRecent = false;
                $scope.recentArticleInfo = data;
            });
        }

        $scope.orderRecentArticles = function(item){
            return $scope.recentArticles.indexOf(item.id.toString());
        };


        // Favorite section

        $scope.loadingFavorites = true;
        FavouriteSectionService.getFavouriteSection().success(function(data){
            $scope.favouriteSection = data;
            searchForArticlesFromFavoriteSection();
        });

        var searchForArticlesFromFavoriteSection = function(){
            if($scope.favouriteSection.section != null){
                ArticleService.search({section: $scope.favouriteSection.section.id, page_size: 9}).success(function(articles){
                    $scope.favSectionArticles = articles;
                    $scope.loadingFavorites = false;
                });
            }else{
                $scope.loadingFavorites = false;
            }
        };

        $scope.updateFavouriteSection = function(){
            FavouriteSectionService.updateFavouriteSection($scope.favouriteSection).success(function (data){
                $scope.favouriteSection = data;
                searchForArticlesFromFavoriteSection();
            });
        };

        ArticleService.getSections().success(function (data) {
            $scope.sections = data;
        });


        // Note

        $scope.noteRules = noteParserRules;

        NoteService.getNote().success(function (data) {
            $scope.note = data;
            $scope.unedited = angular.copy(data);
        });

        $scope.saveNote = function () {
            $scope.savingNote = true;
            NoteService.updateNote($scope.note).success(function (data) {
                $scope.note = data;
                $scope.unedited = angular.copy(data);
                $scope.savingNote = false;
            });
        };


        // Cake diagrams TODO (Could some of this be put into a service?)

        PublicationService.getActive().success(function(data){
            $scope.publication = data;

            $q.all([ PublicationService.getStatusCounts($scope.publication.id),ArticleService.getStatuses()]).then(function(data){
                $scope.statusCounts = data[0].data;
                $scope.statuses = data[1].data;
                var statusArrays = graphInfoToArray($scope.statusCounts, $scope.statuses);
                $scope.statusLabels = statusArrays[0];
                $scope.statusChartColors = statusArrays[1];
                $scope.publication.statusCounts = statusArrays[2];
            });

            $q.all([ PublicationService.getLayoutStatusCounts($scope.publication.id),PublicationService.getLayoutStatuses()]).then(function(data){
                $scope.layoutStatusCounts = data[0].data;
                $scope.layoutStatuses = data[1].data;
                var statusArrays = graphInfoToArray($scope.layoutStatusCounts, $scope.layoutStatuses);
                $scope.layoutStatusLabels = statusArrays[0];
                $scope.layoutStatusChartColors = statusArrays[1];
                $scope.publication.layoutStatusCounts = statusArrays[2];
            });

            $q.all([ PublicationService.getReviewStatusCounts($scope.publication.id),ArticleService.getReviews()]).then(function(data){
                $scope.reviewStatusCounts = data[0].data;
                $scope.reviewStatuses = data[1].data;
                var statusArrays = graphInfoToArray($scope.reviewStatusCounts, $scope.reviewStatuses);
                $scope.reviewStatusLabels = statusArrays[0];
                $scope.reviewStatusChartColors = statusArrays[1];
                $scope.publication.reviewStatusCounts = statusArrays[2];
                console.log(statusArrays);
            });
        });

        function graphInfoToArray(counts, statuses){
            var temp_counts = [];
            var temp_labels = [];
            var temp_colors = [];
            for(var i = 0; i < statuses.length; i++){
                temp_labels.push(statuses[i].name);
                temp_colors.push(statuses[i].color);
                temp_counts.push(counts[statuses[i].id]);
            }
            return [temp_labels, temp_colors, temp_counts];

        }

        //TODO: Refactor when we get back to the cake diagrams
        $scope.isEmptyArray = function(array){
            if(array == undefined || array == null || array == "" || array == []) {
                return true;
            } else {
                var maxFound = 0;
                for(var i = 0; i < Object.keys(array).length;i++){
                    if(array[Object.keys(array)[i]] > maxFound){
                        maxFound = array[i];
                    }
                }
                return maxFound <= 0;
            }
        };

        $scope.countTotals = function(array){
            if(!$scope.isEmptyArray(array)){
                return array.reduce(function(x,y){return x+y},0);
            }
            return 0;
        };

        $scope.clickArticleStatus = function(selected){
            $location.url('artikler?publication=' + $scope.publication.id + '&status=' + ($scope.statuses[selected].id));
            $scope.$apply();
        };

        $scope.clickReviewStatus = function(selected){
            $location.url('artikler?publication=' + $scope.publication.id + '&status=' + ($scope.statuses[selected].id));
            $scope.$apply();
        };

        $scope.clickLayoutStatus = function(selected){
            $location.url('disposisjon');
            $scope.$apply();
        };


        $scope.$on('$locationChangeStart', function (event) {
            if (promptCondition()) {
                if (!confirm("Er du sikker på at du vil forlate siden? Det finnes ulagrede endringer.")) {
                    event.preventDefault();
                }
            }
        });

        window.onbeforeunload = function () {
            if (promptCondition()) {
                return "Det finnes ulagrede endringer.";
            }
        };

        function promptCondition() {
            return $scope.unedited.content != $scope.note.content;
        }

        $scope.$on('$destroy', function() {
            window.onbeforeunload = undefined;
        });
    });