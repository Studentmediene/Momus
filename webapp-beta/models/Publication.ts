import { Model } from './Model';

export interface Publication extends ng.resource.IResource<Publication>, Model {
    name: string;
    release_date: Date;
}

export interface PublicationSerial extends ng.resource.IResource<Publication>, Model {
    name: string;
    release_date: string;
}
