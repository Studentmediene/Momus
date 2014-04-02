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
    .controller('DispositionCtrl', function ($scope, $http) {
        $scope.addArticle = function(page){
            console.log("Add article on page "+ page.pageNr);
        };
        $scope.arrayen = [
            {
                name:"håkon",
                age:24
            },
            {
                name:"bård",
                age:21
            }
        ];
        // adding all journalists that are in a page, looping throu all articles
        $scope.getJournalists = function(page){
//           MER PRIKETE EN JAVA, LAGET LISTA OMIGJEN DA BLE DET NY ITEREASJON
            return $scope.arrayen;
        };
        // Test disposition
        $scope.disposition = {
            id:0,
            publicationNr:3,
            release_date:2014,
            pages:[
                {
                    pageNr:1,
                    articles: [
                        {
                            type:"KulturRaport",
                            name:"Fuglefrø",
                            journalists: [
                                {
                                    name:"håkon",
                                    age:24

                                }
                            ]
                        },
                        {
                            type:"Portrett",
                            name:"Nato Jens"
                        }
                    ]
                },
                {
                    pageNr:2,
                    articles: [
                        {
                            type:"Miljø",
                            name:"Oljesøl"
                        }
                    ]
                }
            ]
        };
});
