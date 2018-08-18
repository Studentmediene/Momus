import { Model } from './Model';

export interface Advert extends ng.resource.IResource<Advert>, Model {
    name: string;
    comment: string;
}
