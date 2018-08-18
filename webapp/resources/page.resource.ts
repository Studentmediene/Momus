import { Page, PageOrder, PageContent } from '../models/Page';

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
type errFunc = (err: any) => void;

export interface PageResource extends ng.resource.IResourceClass<Page> {
    saveMultipleEmpty(
        params: {publicationId: number, afterPage: number, numNewPages: number},
        success?: (pages: Page[]) => void,
        error?: errFunc,
    ): Page[];

    updateMeta(
        params: {},
        body: Page,
        success?: (page: Page) => void,
        error?: errFunc,
    ): Page;

    layoutStatusCounts(
        params: {publicationId: number},
        success?: (counts: any) => void,
        error?: errFunc,
    ): any;

    pageOrder(
        params: {publicationId: number},
        success?: (order: PageOrder) => void,
        error?: errFunc,
    ): PageOrder;

    updatePageOrder(
        params: {},
        body: PageOrder,
        success?: () => void,
        error?: errFunc,
    ): void;

    updateContent(
        params: {pageid: number},
        body: PageContent,
        success?: () => void,
        error?: errFunc,
    ): void;
}
