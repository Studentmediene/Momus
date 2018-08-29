import { Model } from './Model';

export interface Advert extends ng.resource.IResource<Advert>, Model {
    [index: string]: any;
    name: string;
    comment: string;
}
