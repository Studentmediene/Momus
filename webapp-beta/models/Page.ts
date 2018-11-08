import { Model } from './Model';
import { LayoutStatus } from './Statuses';

export interface Page extends ng.resource.IResource<Page>, Model {
    done: boolean;
    articles: number[];
    adverts: number[];
    layout_tatus: LayoutStatus[];
}

export interface PageOrder {
    publication_id: number;
    order: Array<{id: number}>;
}

export interface PageContent {
    page_id: number;
    publication_id: number;
    articles: number[];
    adverts: number[];
}
