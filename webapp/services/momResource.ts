import * as angular from 'angular';

export type MomResourceFactory<T> = (
    url: string,
    paramDefaults: object,
    actions: ng.resource.IActionHash,
    requestTransform?: angular.IHttpRequestTransformer,
    responseTransform?: angular.IHttpResponseTransformer,
    options?: ng.resource.IResourceOptions,
) => ng.resource.IResourceClass<T>;

/* @ngInject */
export default function momResourceFactory(
    $resource: ng.resource.IResourceService,
    RESOURCE_ACTIONS: ng.resource.IActionHash,
    $http: angular.IHttpService,
) {
    return (
        url: string,
        paramDefaults: any,
        actions: ng.resource.IActionHash,
        requestTransform: angular.IHttpRequestTransformer,
        responseTransform: angular.IHttpResponseTransformer,
        options: ng.resource.IResourceOptions,
    ) => {
        actions = withDefaultActions(actions);
        actions = withCustomTransforms(actions, requestTransform, responseTransform);
        return $resource(url, paramDefaults, actions, options);
    };

    function withDefaultActions(actions: ng.resource.IActionHash): ng.resource.IActionHash {
        return {
            ...RESOURCE_ACTIONS,
            ...actions,
        };
    }

    function withCustomTransforms(
        actions: ng.resource.IActionHash,
        requestTransform: angular.IHttpRequestTransformer,
        responseTransform: angular.IHttpResponseTransformer,
    ) {
        Object.keys(actions)
            .filter((k) => !actions[k].skipTransform)
            .forEach((k) => {
                const action = actions[k];
                actions[k] = {
                    ...action,
                    transformRequest: withDefaultRequestTransform(requestTransform),
                    transformResponse: action.isArray
                        ? withDefaultResponseTransform(
                            <T>(items: T[], headers: angular.IHttpHeadersGetter, status: number) =>
                                items.map((item) => responseTransform(item, headers, status),
                            ),
                        )
                        : withDefaultResponseTransform(responseTransform),
                };
        });
        return actions;
    }

    function withDefaultResponseTransform(
        transform: angular.IHttpResponseTransformer,
    ): angular.IHttpResponseTransformer[] {
        let defaults = $http.defaults.transformResponse;
        defaults = angular.isArray(defaults) ? defaults : [defaults];
        return [...defaults, transform];
    }

    function withDefaultRequestTransform(
        transform: angular.IHttpRequestTransformer,
    ): angular.IHttpRequestTransformer[] {
        let defaults = $http.defaults.transformRequest;
        defaults = angular.isArray(defaults) ? defaults : [defaults];

        // Need to apply the toJSON from the resource, since without it the object won't be serialized properly
        // The $resolved and $promise properties won't be stripped
        const fixedTransform = <T>(obj: ng.resource.IResource<T>, headers: angular.IHttpHeadersGetter) => {
            if (!obj) {
                return obj;
            }
            const transformed = transform(obj, headers);
            return {
                ...transformed,
                toJSON: obj.toJSON,
            };
        };
        return [fixedTransform, ...defaults];
    }
}
