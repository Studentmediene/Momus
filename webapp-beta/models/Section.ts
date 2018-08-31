import { Model } from './Model';

export interface Section extends ng.resource.IResource<Section>, Model {
    name: string;
    color: string;
    deleted: boolean;
}
