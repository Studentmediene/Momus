import { Page, PageOrder, PageContent } from '../models/Page';
import { ResourceFunc } from './app.resources';

/* @ngInject */
export default function pageResourceFactory(
    $resource: ng.resource.IResourceService,
): PageResource {
    return <PageResource> $resource('/api/pages/:pageid/:resource',
        {
            pageid: '@id',
        },
        {
            saveMultipleEmpty: { method: 'POST', isArray: true, hasBody: false, params: {pageid: 'empty'} },
            updateMeta: {method: 'PATCH', params: {resource: 'metadata'} },
            layoutStatusCounts: { method: 'GET', params: {pageid: 'layoutstatuscounts'} },
            pageOrder: { method: 'GET', params: {pageid: 'page-order'} },
            updatePageOrder: { method: 'PUT', params: {pageid: 'page-order'} },
            updateContent: { method: 'PUT', params: {resource: 'content'} },
        });
}

export interface PageResource extends ng.resource.IResourceClass<Page> {
    saveMultipleEmpty: ResourceFunc<Page[], null, { publicationId: number, afterPage: number, numNewPages: number }>;
    updateMeta: ResourceFunc<Page, Page>;

    pageOrder: ResourceFunc<ng.resource.IResource<PageOrder>, null, { publicationId: number }>;
    updatePageOrder: ResourceFunc<void, PageOrder>;
    updateContent: ResourceFunc<void, PageContent, { pageid: number }>;

    layoutStatusCounts: ResourceFunc<any, null, { publicationId: number }>;
}
