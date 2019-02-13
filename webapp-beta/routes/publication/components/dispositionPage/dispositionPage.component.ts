import * as angular from 'angular';
import { Advert } from 'models/Advert';
import { Article } from 'models/Article';
import { Page } from 'models/Page';
import { PageResource } from 'resources/page.resource';
import { Publication } from 'models/Publication';
import { ColumnWidths } from '../publicationDisposition/getColumnWidths';
import { OpenNewArticleModal } from 'components/newArticleModal/newArticleModal.component';
import { OpenNewAdvertModal } from 'components/newAdvertModal/newAdvertModal.component';
import { ArticleResource } from 'resources/article.resource';
import { PersonResource } from 'resources/person.resource';

class DispositionPageCtrl implements angular.IController {
    public page: Page;
    public publication: Publication;
    public articles: Article[];
    public adverts: Advert[];
    public onDelete: (page: {page: Page}) => void;

    public articlesLookup: {[index: number]: Article};
    public advertsLookup: {[index: number]: Advert};
    public columnWidths: ColumnWidths;

    public editing: boolean = false;
    public loading: boolean = false;
    public newArticles: Article[];
    public newAdverts: Advert[];

    private articleResource: ArticleResource;
    private pageResource: PageResource;
    private personResource: PersonResource;
    private openNewArticleModal: OpenNewArticleModal;
    private openNewAdvertModal: OpenNewAdvertModal;

    constructor(
        articleResource: ArticleResource,
        pageResource: PageResource,
        personResource: PersonResource,
        openNewArticleModal: OpenNewArticleModal,
        openNewAdvertModal: OpenNewAdvertModal,
    ) {
        this.articleResource = articleResource;
        this.pageResource = pageResource;
        this.personResource = personResource;
        this.openNewArticleModal = openNewArticleModal;
        this.openNewAdvertModal = openNewAdvertModal;
    }

    public applyColumnWidth(column: string, extra = {}) {
        return {...extra, ...this.columnWidths[column]};
    }

    public editPage() {
        this.editing = true;
        this.newAdverts = this.page.adverts.map((aid) => this.advertsLookup[aid]);
        this.newArticles = this.page.articles.map((aid) => this.articlesLookup[aid]);
    }

    public submitPage() {
        this.editing = false;

        this.page.articles = this.newArticles.map((a) => a.id);

        this.page.adverts = this.newAdverts.map((a) => a.id);

        this.loading = true;
        this.pageResource.updateContent(
            {pageid: this.page.id},
            {
                publication_id: this.publication.id,
                page_id: this.page.id,
                articles: this.page.articles,
                adverts: this.page.adverts,
            },
            () => { this.loading = false; },
        );
    }

    public deletePage() {
        this.onDelete({ page: this.page });
    }

    public updatePageMeta() {
        this.loading = true;
        this.pageResource.updateMeta({}, this.page, () => { this.loading = false; });
    }

    public createArticle(page: Page) {
        this.openNewArticleModal({
            persons: this.personResource.query(),
            publication: this.publication,
            publications: [this.publication],
            sections: this.articleResource.sections(),
            types: this.articleResource.types(),
        }).then((article) => {
            this.articlesLookup[article.id] = article;
            this.articles.push(article);
            page.articles.push(article.id);
        });
    }

    public createAdvert(page: Page) {
        this.openNewAdvertModal()
            .then((advert) => {
                this.advertsLookup[advert.id] = advert;
                this.adverts.push(advert);
                page.adverts.push(advert.id);
            });
    }
}
export default angular.module('momusApp.routes.publication.dispositionPage', [])
    .component('dispositionPage', {
        template: require('./dispositionPage.html'),
        controller: DispositionPageCtrl,
        controllerAs: 'vm',
        bindings: {
            number: '<',
            publication: '<',
            page: '<',
            articles: '<',
            columnWidths: '<',
            articleWidth: '<',
            adverts: '<',
            articlesLookup: '<',
            advertsLookup: '<',
            layoutStatuses: '<',
            articleStatuses: '<',
            reviewStatuses: '<',
            onDelete: '&',
        },
    });
