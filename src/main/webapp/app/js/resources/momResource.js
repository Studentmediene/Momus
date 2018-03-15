'use strict';

angular.module('momusApp.resources')
    .factory('momResource', function($resource, RESOURCE_ACTIONS, $http) {
        return (url, paramDefaults, actions, requestTransform, responseTransform, options) => {
            actions = withDefaultActions(actions);
            actions = withCustomTransforms(actions, requestTransform, responseTransform);
            return $resource(url, paramDefaults, actions, options);
        };

        function withDefaultActions(actions) {
            return {
                ...RESOURCE_ACTIONS,
                ...actions
            }
        }

        function withCustomTransforms(actions, requestTransform, responseTransform) {
            Object.keys(actions)
                .filter(k => !actions[k].skipTransform)
                .forEach(k => {
                    const action = actions[k];
                    if(requestTransform){
                        action.transformRequest = withDefaultRequestTransform(requestTransform);
                    }

                    if(responseTransform){
                        if(action.isArray) {
                            action.transformResponse = withDefaultResponseTransform(items => items.map(responseTransform))
                        } else {
                            action.transformResponse = withDefaultResponseTransform(responseTransform);
                        }
                    }
                });
            return actions
        }

        function withDefaultResponseTransform(transform) {
            let defaults = $http.defaults.transformResponse;
            defaults = angular.isArray(defaults) ? defaults : [defaults];
            return defaults.concat(transform);
        }

        function withDefaultRequestTransform(transform) {
            const defaults = $http.defaults.transformRequest;
            return [transform].concat(defaults);
        }
    });
