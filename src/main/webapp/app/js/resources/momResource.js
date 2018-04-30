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
                    actions[k] = {
                        ...action,
                        transformRequest: withDefaultRequestTransform(requestTransform),
                        transformResponse: action.isArray ?
                            withDefaultResponseTransform(items => items.map(responseTransform)) :
                            withDefaultResponseTransform(responseTransform)
                    }
            });
            return actions;
        }

        function withDefaultResponseTransform(transform) {
            let defaults = $http.defaults.transformResponse;
            defaults = angular.isArray(defaults) ? defaults : [defaults];
            return [...defaults, transform];
        }

        function withDefaultRequestTransform(transform) {
            let defaults = $http.defaults.transformRequest;
            defaults = angular.isArray(defaults) ? defaults : [defaults];

            // Need to apply the toJSON from the resource, since without it the object won't be serialized properly
            // The $resolved and $promise properties won't be stripped
            const fixedTransform = obj => {
                if(!obj) return obj;
                const transformed = transform(obj);
                return {
                    ...transformed,
                    toJSON: obj.toJSON
                };
            };
            return [fixedTransform, ...defaults];
        }
    });
