import { Article, ArticleType, ArticleContent } from '../models/Article';
import { ArticleRevision, RevisionDiff } from '../models/ArticleRevision';
import { IPromise, IHttpResponse } from 'angular';
import { ArticleStatus } from '../models/Statuses';
import { ReviewStatus } from '../models/Statuses';
import { Section } from '../models/Section';
import { ResourceFunc } from './app.resources';
import { ArticleSearchParams } from 'models/ArticleSearchParams';

/* @ngInject */
export default function articleResourceFactory(
    $resource: ng.resource.IResourceService,
    $http: ng.IHttpService,
): ArticleResource {
    return <ArticleResource> $resource('/api/article/:id/:resource',
        {
            id: '@id',
        },
        {
            lastArticlesForPerson: { method: 'GET', isArray: true },
            revisions: { method: 'GET', params: { resource: 'revisions' }, isArray: true },
            compareRevisions: { method: 'GET', url: '/api/article/:id/revisions/:rev1/:rev2', isArray: true },
            multiple: { method: 'GET', params: { id: 'multiple' }, isArray: true },
            search: { method: 'POST', params: { id: 'search' }, isArray: true },
            content: { method: 'GET', params: { resource: 'content' } },
            updateMetadata: { method: 'PATCH', params: { resource: 'metadata' } },
            updateStatus: { method: 'PATCH', params: { resource: 'status' } },
            updateNote: { method: 'PATCH', params: { resource: 'note' } },
            archive: { method: 'PATCH', params: { resource: 'archived', archived: true }, hasBody: false },
            restore: { method: 'PATCH', params: { resource: 'archived', archived: false }, hasBody: false },

            types: { method: 'GET', params: { id: 'types' }, isArray: true, cache: true },
            statuses: { method: 'GET', params: { id: 'statuses' }, isArray: true, cache: true },
            reviewStatuses: { method: 'GET', params: { id: 'reviews' }, isArray: true, cache: true },
            sections: { method: 'GET', params: { id: 'sections' }, isArray: true, cache: true },
            statusCounts: { method: 'GET', params: { id: 'statuscounts' } },
            reviewStatusCounts: { method: 'GET', params: { id: 'reviewstatuscounts' } },
        });
}

export interface ArticleResource extends ng.resource.IResourceClass<Article> {
    lastArticlesForPerson: ResourceFunc<Article[], null, { userId: number }>;
    multiple: ResourceFunc<Article[], null, { ids: number[] }>;
    search: ResourceFunc<Article[], ReturnType<ArticleSearchParams["stringify"]>>;
    content: ResourceFunc<ArticleContent, null, { id: number }>;
    updateMetadata: ResourceFunc<Article, Article, { id: number }>;
    updateStatus: ResourceFunc<Article, Article, { id: number}>;
    updateNote: ResourceFunc<Article, string, { id: number}>;
    archive: ResourceFunc<Article, null, { id: number }>;
    restore: ResourceFunc<Article, null, { id: number }>;

    revisions: ResourceFunc<ArticleRevision[], null, { id: number}>;
    compareRevisions: ResourceFunc<RevisionDiff[], null, { id: number, rev1: number, rev2: number }>;

    types: ResourceFunc<ArticleType[]>;
    statuses: ResourceFunc<ArticleStatus[]>;
    reviewStatuses: ResourceFunc<ReviewStatus[]>;
    sections: ResourceFunc<Section[]>;

    statusCounts: ResourceFunc<Map<number, number>, null, { publicationId: number }>;
    reviewStatusCounts: ResourceFunc<Map<number, number>, null, { publicationId: number }>;
}
