'use strict';

angular.module('momusApp.resources')
    .factory('defaultResourceActions', () => {
        return () => ({
            save: {method: 'POST'},
            get:    {method: 'GET'},
            query: {method: 'GET', isArray: true},
            update: {method: 'PUT'},
            delete: {method: 'DELETE'}
        });
    })
    .factory('momResource', function($resource, defaultResourceActions, $http) {
        return (url, paramDefaults, actions, requestTransform, responseTransform, options) => {
            actions = withDefaultActions(actions);
            actions = withCustomTransforms(actions, requestTransform, responseTransform);
            return $resource(url, paramDefaults, actions, options);
        };

        function withDefaultActions(actions) {
            return {
                ...defaultResourceActions(),
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
            return [...defaults, transform];
        }

        function withDefaultRequestTransform(transform) {
            const defaults = $http.defaults.transformRequest;
            return [transform, ...defaults];
        }
    });
