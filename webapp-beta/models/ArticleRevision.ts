import { Model } from './Model';
import { ArticleStatus } from 'models/Statuses';
import { Article } from 'models/Article';

export interface ArticleRevision extends ng.resource.IResource<ArticleRevision>, Model {
    article?: Article;
    content: string;
    status: ArticleStatus;
}

export enum Operation {
    EQUAL = 'EQUAL',
    INSERT = 'INSERT',
    DELETE = 'DELETE',
    INSERTTAG = 'INSERTTAG',
    DELETETAG = 'DELETETAG',
}
export interface RevisionDiff {
    operation: Operation;
    text: string;
}
