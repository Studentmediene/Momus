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

angular.module('momusApp.services').

    factory('HttpInterceptor', function ($q, $location, $injector, $rootScope) {
        /*
         This interceptor will intercept http requests that have failed.

         If the reason for failing is 401 it means we are not logged in on our server.
         So we show a login form and wait for the LoginCtrl to tell us that the user is logged in.
         All failed 401 requests will be put in a buffer so they can be resent when logged in.

         If the reason is something else, a modal will pop up with an error message.
         */


        // Make sure we only send one request for login form
        var hasRequestedLogin = false;

        // Buffer holding all requests that failed
        var resendBuffer = [];

        function addToBuffer(request, deferred) {
            resendBuffer.push({
                request: request,
                deferred: deferred
            });
        }

        function resendAllInBuffer() {
            for (var i = 0; i < resendBuffer.length; i++) {
                resendRequest(resendBuffer[i].request, resendBuffer[i].deferred);
            }
            resendBuffer = [];
        }

        function resendRequest(request, deferred) {
            var $http = $injector.get('$http');
            $http(request).then(
                function (response) {
                    deferred.resolve(response);
                },
                function (response) {
                    deferred.reject(response);
                }
            );
        }


        function requestLogin() {
            hasRequestedLogin = true;

            $rootScope.$broadcast('showLogin');
        }

        $rootScope.$on('loginComplete', function() {
            hasRequestedLogin = false;
            resendAllInBuffer();
        });

        return {
            'responseError': function (response) {
                // Allow $http requests to handle errors themselves
                if (response.config.bypassInterceptor) {
                    return $q.reject(response);
                }


                // is the problem we're not logged in?
                if (response.status === 401) {

                    // show login form if we haven't already
                    if (!hasRequestedLogin) {
                        requestLogin();
                    }

                    // add the request to the buffer to be sent later
                    var deferred = $q.defer();
                    addToBuffer(response.config, deferred);
                    return deferred.promise;

                } else {
                    // show an error message
                    var errorMessage = '';

                    if (response.data.error) {
                        errorMessage = response.data.error;
                    }
                    var MessageModal = $injector.get('MessageModal');
                    MessageModal.error(errorMessage, true);

                }
                return $q.reject(response);
            }
        };
    });
