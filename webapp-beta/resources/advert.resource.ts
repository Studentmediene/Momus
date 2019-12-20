import { Advert } from '../models/Advert';
import { ResourceFunc } from './app.resources';

/* @ngInject */
export default function advertResourceFactory(
    $resource: ng.resource.IResourceService,
): AdvertResource {
    return <AdvertResource> $resource('/api/adverts/:id/:resource',
        {
            id: '@id',
        },
        {
            updateComment: { method: 'PATCH', params: {resource: 'comment'} },
        });
}

export interface AdvertResource extends ng.resource.IResourceClass<Advert> {
    updateComment: ResourceFunc<Advert, string, {id: number}>;
}
