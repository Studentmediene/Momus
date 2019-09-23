import { Article, ArticleType, ArticleContent } from '../models/Article';
import { ArticleRevision, RevisionDiff } from '../models/ArticleRevision';
import { IPromise, IHttpResponse } from 'angular';
import { ArticleStatus } from '../models/Statuses';
import { ReviewStatus } from '../models/Statuses';
import { Section } from '../models/Section';

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

type errFunc = (err: any) => void;

export interface ArticleResource extends ng.resource.IResourceClass<Article> {

    lastArticlesForPerson(
        params: { userId: number },
        success?: (articles: Article[]) => void,
        error?: errFunc,
    ): Article[];

    revisions(
        params: { id: number },
        success?: (revisions: ArticleRevision[]) => void,
        error?: errFunc,
    ): ArticleRevision[];

    compareRevisions(
        params: { id: number, rev1: number, rev2: number },
        success?: (diffs: object[]) => void,
        error?: errFunc,
    ): RevisionDiff[];

    multiple(
        params: { ids: number[] },
        success?: (articles: Article[]) => void,
        error?: errFunc,
    ): Article[];

    search(
        params: {},
        body: object,
        success?: (articles: Article[]) => void,
        error?: errFunc,
    ): Article[];

    content(
        params: {id: number},
        success?: (content: ArticleContent) => void,
        error?: errFunc,
    ): ArticleContent;

    updateMetadata(
        params: { id: number },
        body: Article,
        success?: (article: Article) => void,
        error?: errFunc,
    ): Article;

    updateStatus(
        params: { id: number },
        body: Article,
        success?: (article: Article) => void,
        error?: errFunc,
    ): Article;

    updateNote(
        params: { id: number },
        body: string,
        success?: (article: Article) => void,
        error?: errFunc,
    ): Article;

    archive(
        params: { id: number },
        success?: (article: Article) => void,
        error?: errFunc,
    ): Article;

    restore(
        params: { id: number },
        success?: (article: Article) => void,
        error?: errFunc,
    ): Article;

    types(params?: {}, success?: (types: ArticleType[]) => void, error?: errFunc): ArticleType[];
    statuses(params?: {}, success?: (types: ArticleStatus[]) => void, error?: errFunc): ArticleStatus[];
    reviewStatuses(params?: {}, success?: (types: ReviewStatus[]) => void, error?: errFunc): ReviewStatus[];
    sections(params?: {}, success?: (types: Section[]) => void, error?: errFunc): Section[];

    statusCounts(
        params: { publicationId: number },
        success?: (counts: Map<number, number>) => void,
        error?: errFunc,
    ): Map<number, number>;

    reviewStatusCounts(
        params: { publicationId: number },
        success?: (counts: Map<number, number>) => void,
        error?: errFunc,
    ): Map<number, number>;
}
