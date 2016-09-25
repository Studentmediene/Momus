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

angular.module('momusApp.services')
    .service('LandingChanger', function($route, PersonService, $location){
        return {
            //Sets the home page to the page stored for the logged in user
            setLanding: function(){
                var currentRoute = $location.path(); //Get the current route, to redirect after getting landing
                PersonService.getLandingPage().success(function(data){
                    if(data == null || data == ""){
                        return;
                    }
                    var landing = data.page;
                    $route.routes[null] = angular.extend(
                        {
                            redirectTo: '/' + landing,
                            reloadOnSearch: true,
                            caseInsensitiveMath: false
                        });
                    $location.path(currentRoute);
                });

            },
            getTopLevelRoutes: function() {
                var routes = [];
                for (var route in $route.routes) {
                    if (route.substring(0, 1) != "/") {
                        continue
                    }
                    route = route.substr(1);
                    route = route.split("/");
                    if (route.length == 1) {
                        routes.push(route[0]);
                    }
                }
                return routes;
            }
        };
    });
