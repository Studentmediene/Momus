import { Advert } from '../models/Advert';

/* @ngInject */
export default function advertResourceFactory(
    $resource: ng.resource.IResourceService,
): AdvertResource {
    return <AdvertResource> $resource('/api/advert/:id/:resource',
        {
            id: '@id',
        },
        {
            updateComment: { method: 'PATCH', params: {resource: 'comment'} },
        });
}
type errFunc = (err: any) => void;
export interface AdvertResource extends ng.resource.IResourceClass<Advert> {
    updateComment(
        params: {id: number},
        body: string,
        success?: (advert: Advert) => void,
        err?: errFunc,
    ): Advert;
}
