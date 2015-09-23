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
        $scope.pubId = $routeParams.id;

        PublicationService.getById($scope.pubId).success(function(data){
            $scope.publication = data;

            console.log(data);

            if(data) {
                ArticleService.search({publication: $scope.pubId}).success(function (data) {
                    $scope.publication.articles = data;
                });
                PublicationService.getPages(data.id).success(function (data){
                    $scope.publication.pages = data;
                })
            }
        });

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
                console.log(data);
            })
        };

    });
