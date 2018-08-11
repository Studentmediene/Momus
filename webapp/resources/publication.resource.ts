import { Publication, PublicationSerial } from '../models/Publication';
import { MomResourceFactory } from '../services/momResource';
import { LayoutStatus } from 'models/Statuses';

/* @ngInject */
export default function publicationResourceFactory(momResource: MomResourceFactory<Publication>): PublicationResource {
    return <PublicationResource> momResource('/api/publications/:id/:resource',
        {
            id: '@id',
        },
        {
            active: { method: 'GET', params: {id: 'active'}, bypassInterceptor: true },
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
    active(
        params?: {},
        success?: (publication: Publication) => void,
        error?: (err: any) => void,
    ): Publication;

    layoutStatuses(
        params?: {},
        success?: (layoutStatuses: LayoutStatus[]) => void,
        error?: (err: any) => void,
    ): void;
}
