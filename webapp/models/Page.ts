import { Model } from './Model';
import { Article } from './Article';
import { Advert } from './Advert';
import { LayoutStatus } from './Statuses';

export interface Page extends ng.resource.IResource<Page>, Model {
    done: boolean;
    articles: Article[];
    adverts: Advert[];
    layout_tatus: LayoutStatus[];
}

export interface PageOrder {
    publication_id: number;
    order: number[];
}

export interface PageContent {
    page_id: number;
    publication_id: number;
    articles: Article[];
    adverts: Advert[];
}
