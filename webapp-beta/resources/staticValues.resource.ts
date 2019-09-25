import { Env } from 'app.types';
import { ResourceFunc } from './app.resources';

/* @ngInject */
export default function pageResourceFactory(
    $resource: ng.resource.IResourceService,
): StaticValuesResource {
    return <StaticValuesResource> $resource('/api/static-values/:resource',
            {},
            {
                environment: { method: 'GET' , params: {resource: 'env'}, cache: true },
                roleNames: { method: 'GET' , params: {resource: 'role-names'}, cache: true },
            });
}

export interface StaticValuesResource extends ng.resource.IResourceClass<any> {
    roleNames: ResourceFunc<object>;
    environment: ResourceFunc<Env>;
}
