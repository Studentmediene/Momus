import { Person } from '../models/Person';
import { ResourceFunc } from './app.resources';

/* @ngInject */
export default function personResourceFactory($resource: ng.resource.IResourceService): PersonResource {
    return <PersonResource> $resource('/api/person/:id/:resource',
        {
            id: '@id',
        },
        {
            query: { method: 'GET', isArray: true, cache: true },
            me: { method: 'GET', params: {id: 'me'}, cache: true },
            updateFavouritesection: {
                method: 'PATCH',
                params: {id: 'me', resource: 'favouritesection'},
                hasBody: false,
            },
            loggedIn: { method: 'GET', params: {resource: 'loggedin'}, isArray: true },
            updateSessionState: {method: 'PUT', url: '/api/person/sessions/:id', hasBody: false },
        });
}

export interface PersonResource extends ng.resource.IResourceClass<Person> {
    me: ResourceFunc<Person>;
    updateFavouritesection: ResourceFunc<void, null, { section: number }>;
    loggedIn: ResourceFunc<Person[], null, { state: string}>;
    updateSessionState: ResourceFunc<void, null, { id: string, state: string}>;
}
