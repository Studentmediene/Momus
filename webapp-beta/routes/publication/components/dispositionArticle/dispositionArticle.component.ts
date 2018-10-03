import * as angular from 'angular';
import { Article } from 'models/Article';
import { ArticleResource } from 'resources/article.resource';
import { ColumnWidths } from '../publicationDisposition/getColumnWidths';

class DispositionArticleCtrl implements angular.IController {
    public article: Article;

    public showButtonRow: boolean = false;
    public columnWidths: ColumnWidths;

    private articleResource: ArticleResource;

    constructor(articleResource: ArticleResource) {
        this.articleResource = articleResource;
    }

    public applyColumnWidth(column: string, extra = {}) {
        return {...extra, ...this.columnWidths[column]};
    }

    public updateArticle() {
        this.articleResource.updateStatus({ id: this.article.id }, this.article);
    }

    public submitField(field: string, value: any) {
        this.article[field] = value;
    }

    public toggleButtonRow() {
        this.showButtonRow = !this.showButtonRow;
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
        },
        controller: DispositionArticleCtrl,
        controllerAs: 'vm',
    });
