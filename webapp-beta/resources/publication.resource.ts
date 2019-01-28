import { Publication, PublicationSerial, SimplePublication } from '../models/Publication';
import { MomResourceFactory } from 'services/momResource.factory';
import { LayoutStatus } from 'models/Statuses';

/* @ngInject */
export default function publicationResourceFactory(momResource: MomResourceFactory<Publication>): PublicationResource {
    return <PublicationResource> momResource('/api/publications/:id/:resource',
        {
            id: '@id',
        },
        {
            updateMetadata: { method: 'PATCH', params: {resource: 'metadata'} },
            active: { method: 'GET', params: {id: 'active'} },
            layoutStatuses: {
                method: 'GET', isArray: true, params: {id: 'layoutstatuses'}, cache: true, skipTransform: true,
            },
        },
        publicationRequestTransform,
        publicationResponseTransform,
    );
}

function publicationResponseTransform(publication: PublicationSerial): Publication {
    if (!publication) {
        return null;
    }
    return {
        ...publication,
        release_date: new Date(publication.release_date),
    };
}

function publicationRequestTransform(publication: Publication): PublicationSerial {
    if (!publication) {
        return null;
    }
    return {
        ...publication,
        release_date: publication.release_date.toISOString(),
    };
}

export interface PublicationResource extends ng.resource.IResourceClass<Publication> {
    updateMetadata(
        params: {},
        body: Publication,
        success?: (publication: Publication) => void,
        error?: (err: any) => void,
    ): Publication;
    active(
        params?: {resource: string},
        success?: (publication: SimplePublication) => void,
        error?: (err: any) => void,
    ): SimplePublication;

    layoutStatuses(
        params?: {},
        success?: (layoutStatuses: LayoutStatus[]) => void,
        error?: (err: any) => void,
    ): LayoutStatus[];
}
