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
    .controller('FrontPageCtrl', function ($scope, NoteService, noteParserRules, PersonService, ArticleService, TipAndNewsService, ViewArticleService, FavouriteSectionService, PublicationService, $location, $document) {
        $scope.noteRules = noteParserRules;

        $scope.recentArticles = ViewArticleService.getRecentViews();
        if($scope.recentArticles){
            $scope.loadingRecent = true;
            ArticleService.getMultiple($scope.recentArticles).success(function(data){
                $scope.loadingRecent = false;
                $scope.recentArticleInfo = data;
            });
        }

        PublicationService.getActive().success(function(data){
            $scope.publication = data;
            PublicationService.getStatusCounts($scope.publication.id).success(function(data){
                $scope.publication.statusCounts = data;
            });
            PublicationService.getLayoutStatusCounts($scope.publication.id).success(function(data){
                $scope.publication.layoutStatusCounts = data;
            });
            PublicationService.getReviewStatusCounts($scope.publication.id).success(function(data){
                $scope.publication.reviewStatusCounts = data;
            });
        });

        $scope.orderRecentArticles = function(item){
            return $scope.recentArticles.indexOf(item.id.toString());
        };

        $scope.randomTip = function() {
            $scope.tip = TipAndNewsService.getRandomTip();
        };

        $scope.randomTip();

        $scope.news = TipAndNewsService.getNews();

        NoteService.getNote().success(function (data) {
            $scope.note = data;
            $scope.unedited = angular.copy(data);
        });

        ArticleService.getSections().success(function (data) {
            $scope.sections = data;
        });

        ArticleService.getStatuses().success(function (data){
            $scope.statuses = data;
            $scope.statusLabels = [];
            $scope.statusChartColors = [];
            for(var i = 0; i < $scope.statuses.length; i++){
                $scope.statusLabels.push($scope.statuses[i].name);
                $scope.statusChartColors.push($scope.statuses[i].color);
            }
        });

        ArticleService.getReviews().success(function (data){
            $scope.reviews = data;
            $scope.reviewLabels = [];
            $scope.reviewChartColors = [];
            for(var i = 0; i < $scope.reviews.length; i++){
                $scope.reviewLabels.push($scope.reviews[i].name);
                $scope.reviewChartColors.push($scope.reviews[i].color);
            }
        });

        PublicationService.getLayoutStatuses().success(function(data){
            $scope.layoutStatuses = data;
            $scope.layoutStatusLabels = [];
            $scope.layoutStatusChartColors = [];
            for(var i = 0; i < $scope.layoutStatuses.length; i++){
                $scope.layoutStatusLabels.push($scope.layoutStatuses[i].name);
                $scope.layoutStatusChartColors.push($scope.layoutStatuses[i].color);
            }
        });

        $scope.loadingFavorites = true;
        FavouriteSectionService.getFavouriteSection().success(function(data){
            $scope.favouriteSection = data;
            searchForArticlesFromFavoriteSection();
        });

        $scope.updateFavouriteSection = function(){
            FavouriteSectionService.updateFavouriteSection($scope.favouriteSection).success(function (data){
                $scope.favouriteSection = data;
                searchForArticlesFromFavoriteSection();
            });
        };

        var searchForArticlesFromFavoriteSection = function(){
            if($scope.favouriteSection.section != null){
                ArticleService.search({section: $scope.favouriteSection.section.id, page_size: 9}).success(function(articles){
                    $scope.favSectionArticles = articles;
                    $scope.loadingFavorites = false;
                });
            }
        };

        $scope.saveNote = function () {
            $scope.savingNote = true;
            NoteService.updateNote($scope.note).success(function (data) {
                $scope.note = data;
                $scope.unedited = angular.copy(data);
                $scope.savingNote = false;
            });
        };

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

        $scope.isEmptyArray = function(array){
            if(array == undefined || array == null || array == "" || array == []) {
                return true;
            } else {
                var maxFound = 0;
                for(var i = 0; i < array.length;i++){
                    if(array[i] > maxFound){
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

        $scope.$on('$destroy', function() {
            window.onbeforeunload = undefined;
        });

        function promptCondition() {
            return $scope.unedited.content != $scope.note.content;
        }
    });