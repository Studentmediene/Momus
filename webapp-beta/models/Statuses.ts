import { Model } from './Model';

export interface Status extends ng.resource.IResource<Status>, Model {
    name: string;
    color: string;
    status_order?: string;
}

export interface ArticleStatus extends Status {
    yo: string;
}

export interface LayoutStatus extends Status {
    meh: string;
}

export interface ReviewStatus extends Status {
    test: string;
}
