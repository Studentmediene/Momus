import { Person } from '../models/Person';

/* @ngInject */
export default function personResourceFactory($resource: ng.resource.IResourceService): PersonResource {
    return <PersonResource> $resource('/api/person/:id/:resource',
        {
            id: '@id',
        },
        {
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
    me(
        params?: {},
        success?: (person: Person) => void,
        error?: (err: any) => void,
    ): Person;

    updateFavouritesection(
        params: {section: number},
        success?: () => void,
        error?: (err: any) => void,
    ): void;

    loggedIn(
        params: {state: string},
        success?: (active: Person[]) => void,
        error?: (err: any) => void,
    ): Person[];

    updateSessionState(
        params: {id: string, state: string},
        success?: () => void,
        error?: (err: any) => void,
    ): void;
}
