import { Section } from '../models/Section'
import {ResourceFunc} from "./app.resources";

/* @ngInject */
export default function sectionResourceFactory(
    $resource: ng.resource.IResourceService,
    $http: ng.IHttpService
): SectionResource {
    return <SectionResource> $resource('/api/section/:id/:resource',
        {
            id: '@id',
        }, {
            updateRoles: { method: 'PATCH', params: {resource: 'roles'} }
        });
}

export interface SectionResource extends ng.resource.IResourceClass<Section> {
    updateRoles: ResourceFunc<Section>;
}