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
    .controller('DispositionCtrl', function ($scope, $http, $routeParams, $modal) {
        $scope.addArticle = function(){
            console.log("TRYKKER PÅ NOE");
//            $scope.disposition.pages[page.pageNr-1].articles.push;
        };


        $scope.articleModal = function (page) {

            var modalInstance = $modal.open({
                templateUrl: 'partials/disposition/articleModal.html',
                controller: ModalInstanceCtrl,
                resolve: {
                    articles: function () {
                        return $scope.articles;
                    },
                    page: function () {
                        return page;
                    }
                }

                });

        };
        var ModalInstanceCtrl = function ($scope, $modalInstance, articles, page) {

            if(articles.length != 0){
                $scope.addArticleModel = articles[0];
            }
            $scope.page = page;
            $scope.articles = articles;
            // if the page.articles is not empty, set a default value for the model
            if($scope.page.articles.length != 0){
                $scope.delArticleModel = $scope.page.articles[0];
            }

            $scope.addArticle = function(articleModel){
                if(!articleModel){
                    return;
                }
//                if($scope.page.articles.indexOf(articleModel) == -1){
                var pageAricles = $scope.page.articles;
                for(var i=0; i < pageAricles.length; i++){
                    if(pageAricles[i].id == articleModel.id){
                        return ;
                    }
                }
                $scope.page.articles.push(articleModel);

//                };
            };
            $scope.removeArticle = function(articleModel){
                if(articleModel){
                    var pageAricles = $scope.page.articles;
                    for(var i=0; i < pageAricles.length; i++){
                        if(pageAricles[i].id == articleModel.id){
                            $scope.page.articles.splice(i,1);
                            return ;
                        }
                    }

                }
            };


        };

//        $http.get('/api/publication/'+$routeParams.id).success(function(data) {
//            $scope.articles = data;
//            console.log(data);
//        });
        $scope.articles = [
            {
                id: 1,
                type:"KulturRaport",
                name:"Fuglefrø",
                status: "Skrives",
                photoStatus: "tatt",
                advertisement: false,
                photographers: [
                    {
                        name:"jon",
                        age:24
                    },
                    {
                        name:"birger",
                        age:22
                    },
                    {
                        name:"olav",
                        age:45
                    },
                    {
                        name:"kåre",
                        age:45
                    }
                ],
                journalists: [
                    {
                        name:"håkon",
                        age:24
                    },
                    {
                        name:"bård",
                        age:22
                    },
                    {
                        name:"stian",
                        age:45
                    }
                ]
            },
            {
                id: 2,
                type:"Portrett",
                name:"Nato Jens",
                status: "Desk",
                photoStatus: "lagt inn",
                advertisement: true,
                photographers: [],
                journalists: [
                    {
                        name:"ole",
                        age:20
                    }
                ]
            },
            {
                id: 3,
                type:"Miljø",
                name:"Oljesøl",
                status: "Skrives",
                photoStatus: "redigeres",
                advertisement: false,
                photographers: [],
                journalists: [
                    {
                        name:"frode",
                        age:28
                    }
                ]
            }

        ];

        // Test disposition
        $scope.disposition = {
            id: $routeParams.id,
            publicationNr:3,
            release_date:2014,
            pages:[
                {
                    pageNr:1,
                    section: "Kultur",
                    articles: [
                        {
                            id:1,
                            type:"KulturRaport",
                            name:"Fuglefrø",
                            status: "Skrives",
                            photoStatus: "tatt",
                            advertisement: false,
                            photographers: [
                                {
                                    name:"jon",
                                    age:24
                                },
                                {
                                    name:"birger",
                                    age:22
                                },
                                {
                                    name:"olav",
                                    age:45
                                },
                                {
                                    name:"kåre",
                                    age:45
                                }
                            ],
                            journalists: [
                                {
                                    name:"håkon",
                                    age:24
                                },
                                {
                                    name:"bård",
                                    age:22
                                },
                                {
                                    name:"stian",
                                    age:45
                                }
                            ]
                        },
                        {
                            id: 2,
                            type:"Portrett",
                            name:"Nato Jens",
                            status: "Desk",
                            photoStatus: "lagt inn",
                            advertisement: true,
                            photographers: [],
                            journalists: [
                                {
                                    name:"ole",
                                    age:20
                                }
                            ]
                        }
                    ]
                },
                {
                    pageNr:2,
                    section: "",
                    articles: [
                        {
                            id: 3,
                            type:"Miljø",
                            name:"Oljesøl",
                            status: "Skrives",
                            photoStatus: "redigeres",
                            advertisement: false,
                            photographers: [],
                            journalists: [
                                {
                                    name:"frode",
                                    age:28
                                }
                            ]
                        }
                    ]
                },
                {
                    pageNr:3,
                    section: "",
                    articles: [

                    ]
                },
                {
                    pageNr:4,
                    section: "",
                    articles: [

                    ]
                }
            ]
        };
});
