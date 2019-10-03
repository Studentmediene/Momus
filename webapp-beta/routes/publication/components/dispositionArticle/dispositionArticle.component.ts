import * as angular from 'angular';
import { Article } from 'models/Article';
import { ArticleResource } from 'resources/article.resource';
import { ColumnWidths } from '../publicationDisposition/getColumnWidths';

import './dispositionArticle.scss';

class DispositionArticleCtrl implements angular.IController {
    public article: Article;

    public showButtonRow: boolean = false;
    public columnWidths: ColumnWidths;

    private articleResource: ArticleResource;

    constructor(articleResource: ArticleResource) {
        this.articleResource = articleResource;
    }

    public applyColumnWidth(column: string, extra = {}) {
        return { ...extra, ...this.columnWidths[column] };
    }

    public updateArticle() {
        this.articleResource.updateStatus({ id: this.article.id }, this.article);
    }

    public submitField(field: string, value: any) {
        this.article[field] = value;
        this.updateArticle();
    }

    public toggleButtonRow() {
        this.showButtonRow = !this.showButtonRow;
    }

    public hasJournalists() {
        const article = this.article;
        return article != null && (article.journalists.length > 0
            || (article.external_author != null && article.external_author !== ''));
    }

    public hasPhotographers() {
        const article = this.article;
        return article != null && (article.photographers.length > 0
            || (article.external_photographer != null && article.external_photographer !== ''));
    }

    public hasGraphics() {
        const article = this.article;
        return article != null && article.graphics.length > 0;
    }
}

export default angular.module('momusApp.routes.publication.dispositionArticle', [])
    .component('dispositionArticle', {
        template: require('./dispositionArticle.html'),
        bindings: {
            article: '<',
            articleStatuses: '<',
            reviewStatuses: '<',
            columnWidths: '<',
            // Boolean indicating if this article is a continuation of the same article on prev page,
            // and if so should hide some columns.
            continuation: '<',
        },
        controller: DispositionArticleCtrl,
        controllerAs: 'vm',
    });
