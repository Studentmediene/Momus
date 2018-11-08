import * as angular from 'angular';
import { ArticleResource } from 'resources/article.resource';
import { Article } from 'models/Article';
import { RevisionDiff, ArticleRevision } from 'models/ArticleRevision';

import './articleRevisions.scss';

/* @ngInject */
class ArticleRevisionsCtrl implements angular.IController {
    public article: Article;
    public revisions: ArticleRevision[];
    public showDiff: boolean = false;
    public diffs: RevisionDiff[] = [];
    public currentRevision: ArticleRevision;
    public comparison: {rev1: number, rev2: number} = {rev1: 0, rev2: 0};

    private articleResource: ArticleResource;

    constructor(articleResource: ArticleResource) {
        this.articleResource = articleResource;
    }

    public $onInit() {
        this.currentRevision = this.revisions[0];
    }

    public gotoRev(rev: ArticleRevision) {
        this.currentRevision = rev;
    }

    public toggleComparison() {
        this.showDiff = !this.showDiff;
        this.comparison = {rev1: this.currentRevision.id, rev2: this.currentRevision.id};
        this.getDiffs();
    }

    public getDiffs() {
        const params = {
            id: this.article.id,
            ...this.comparison,
        };

        this.diffs = this.articleResource.compareRevisions(params);
    }
}
export default angular.module('momusApp.routes.article.articleRevisions', [])
    .component('articleRevisions', {
        template: require('./articleRevisions.html'),
        controller: ArticleRevisionsCtrl,
        controllerAs: 'vm',
        bindings: {
            article: '<',
            revisions: '<',
        },
    });
