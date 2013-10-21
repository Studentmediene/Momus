/*
 * Copyright 2013 Studentmediene i Trondheim AS
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
    factory('SmmDbTicket', function($q, $injector) {

        // Make sure we only send one request to SmmDb
        var hasSentRequestForTicket = false;

        // Buffer holding all requests that failed
        var resendBuffer = [];

        function addToBuffer(request, deferred) {
            resendBuffer.push({
                request: request,
                deferred: deferred
            })
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
                function(response) {
                    deferred.resolve(response);
                },
                function(response) {
                    deferred.reject(response);
                }
            );
        }

        function tryLoginThroughSmmDb() {
            hasSentRequestForTicket = true;
            var $http = $injector.get('$http');
            getTicketFromSmmDb($http).success(function(smmDbData) {

                // We are logged in at SmmDb, should validate the ticket with our server
                validateTicket($http, smmDbData.ticket).success(function(loginResponse) {
                    // Success, should resend the requests that have failed
                    resendAllInBuffer();
                    hasSentRequestForTicket = false;
                })
                .error(function(loginResponse) {
                    // Couldn't validate ticket on the server
                    // TODO: Handle properly
                    alert(loginResponse.message);
                });
            })
            .error(function(smmDbData) {
                // error from SmmDb, means we're not logged in there, so we redirect to the login-form
                window.location = 'http://m.studentmediene.no/api/login?next=' + encodeURIComponent(window.location.href);;
            });
        }

        function getTicketFromSmmDb($http) {
            return $http.get('http://m.studentmediene.no/api/ticket/get', {withCredentials: true});
        }

        function validateTicket($http, ticket) {
            // convert the ticket to a string. Change all " with \", and add " around it.
            var ticketString = '"' + JSON.stringify(ticket).replace(/"/g, '\\"') + '"';
            return $http.post('/api/auth/login', ticketString);
        }


        return {
            'responseError': function(response) {
                if (response.status === 401 && response.config.url !== 'http://m.studentmediene.no/api/ticket/get') {

                    if (!hasSentRequestForTicket) {
                        tryLoginThroughSmmDb();
                    }

                    // add the request to the buffer to be sent later
                    var deferred = $q.defer();
                    addToBuffer(response.config, deferred);
                    return deferred.promise;
                }

                return $q.reject(response);
            }
        }
    });
