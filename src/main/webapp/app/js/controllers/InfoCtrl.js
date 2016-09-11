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
    .controller('InfoCtrl', function($scope, TipAndNewsService, PersonService, $rootScope){
        $scope.randomTip = function() {
            $scope.tip = TipAndNewsService.getRandomTip();
        };

        $scope.randomTip();

        $scope.news = TipAndNewsService.getNews();

        $scope.landing = "";
        $scope.landings = ['disposition', 'artikler', 'utgaver', 'info'];

        $scope.getLanding = function() {
            PersonService.getLandingPage().success(function(data){
                if(data != null) {
                    $scope.landing = data.page;
                }else{
                    $scope.landing = '';
                }
            })
        };

        $scope.updateLanding = function(){
            console.log($scope.landing);
            PersonService.updateLandingPage($scope.landing).success(function(data){
                $scope.landing = data.page;
                $rootScope.$broadcast("updatedLanding", data)

            });
        }
    });