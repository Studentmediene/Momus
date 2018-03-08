'use strict';

angular.module('momusApp.resources')
    .factory('transformResponse', function($http) {
        return (transform) => {
            let defaults = $http.defaults.transformResponse;
            defaults = angular.isArray(defaults) ? defaults : [defaults];
            return defaults.concat(transform);
        }
    })
    .factory('transformRequest', function($http) {
        return (transform) => {
            const defaults = $http.defaults.transformRequest;
            return [transform].concat(defaults);
        }
    })
    .factory('momResource', function($resource, RESOURCE_ACTIONS, transformRequest, transformResponse) {
        return (url, paramDefaults, actions, requestTransform, responseTransform, options) => {
            const addRequestTransform = action => {
                action.transformRequest = transformRequest(requestTransform);
            };

            const addResponseTransform = action => {
                if(action.isArray) {
                    action.transformResponse = transformResponse((items) =>
                        items.map(responseTransform)
                    )
                }else {
                    action.transformResponse = transformResponse(responseTransform);
                }
            };

            const defaultActions = RESOURCE_ACTIONS;
            Object.keys(defaultActions).forEach(defaultAction => {
                if(!actions[defaultAction]){
                    actions[defaultAction] = {...defaultActions[defaultAction]};
                }
            });


            Object.keys(actions)
                .filter(action => !actions[action].skipTransform)
                .forEach(action => {
                    if(requestTransform) addRequestTransform(actions[action]);
                    if(responseTransform) addResponseTransform(actions[action]);
                });

            return $resource(url, paramDefaults, actions, options);
        }
    });