import { Model } from './Model';
import { Page } from 'models/Page';
import { Article } from 'models/Article';

export interface Publication extends ng.resource.IResource<Publication>, Model {
    name: string;
    release_date: Date;
    pages: Page[];
    articles: Article[];
}

export interface SimplePublication extends ng.resource.IResource<SimplePublication>, Model {
    name: string;
    release_date: Date;
}

export interface PublicationSerial extends ng.resource.IResource<Publication>, Model {
    name: string;
    release_date: string;
    pages: Page[];
    articles: Article[];
}
