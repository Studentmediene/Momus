import * as angular from 'angular';
import { PageResource } from 'resources/page.resource';
import { ArticleResource } from 'resources/article.resource';
import { AdvertResource } from 'resources/advert.resource';
import { Publication } from 'models/Publication';
import { PageOrder, Page } from 'models/Page';
import { Advert } from 'models/Advert';
import { Article } from 'models/Article';
import { Session } from 'services/session.service';
import { Person } from 'models/Person';
import {
    GetDispWidth,
    ColumnWidths,
} from 'routes/publication/components/publicationDisposition/publicationDisposition.component';
import { OpenNewArticleModal } from 'components/newArticleModal/newArticleModal.component';
import { toIdLookup } from 'utils';
import { OpenNewAdvertModal } from 'components/newAdvertModal/newAdvertModal.component';

interface PageScope extends angular.IScope {
    newArticles: Article[];
    newAdverts: Advert[];
    page: Page;
    editPage: boolean;
}

interface ArticleScope extends angular.IScope {
    showButtonRow: boolean;
}

/* @ngInject */
export default class PublicationDispositionCtrl implements angular.IController {
    public maxNewPages: number = 100;
    public publication: Publication;
    public adverts: Advert[];
    public pageOrder: PageOrder;
    public session: Session;

    public presentUsers: Person[];
    public loading: boolean = false;

    public dispWidth: number = 0;
    public articleWidth: number = 0;
    public columnWidths: ColumnWidths = {};
    public toolbarStyle: object = {};

    public pagesLookup: { [index: number]: Page };
    public articlesLookup: { [index: number]: Article };
    public advertsLookup: { [index: number]: Advert };

    public openButtonRows: ArticleScope[];

    private pageResource: PageResource;
    private articleResource: ArticleResource;
    private advertResource: AdvertResource;
    private getDispWidth: GetDispWidth;

    private openNewArticleModal: OpenNewArticleModal;
    private openNewAdvertModal: OpenNewAdvertModal;

    private $scope: angular.IScope;
    private $timeout: angular.ITimeoutService;
    private $window: angular.IWindowService;

    constructor(
        $scope: angular.IScope,
        $timeout: angular.ITimeoutService,
        $window: angular.IWindowService,
        $log: angular.ILogService,
        // MessageModal,
        pageResource: PageResource,
        articleResource: ArticleResource,
        advertResource: AdvertResource,
        getDispWidth: GetDispWidth,
        openNewArticleModal: OpenNewArticleModal,
        openNewAdvertModal: OpenNewAdvertModal,
    ) {
        this.pageResource = pageResource;
        this.advertResource = advertResource;
        this.articleResource = articleResource;
        this.getDispWidth = getDispWidth;
        this.openNewArticleModal = openNewArticleModal;
        this.openNewAdvertModal = openNewAdvertModal;

        this.$scope = $scope;
        this.$timeout = $timeout;
        this.$window = $window;

        this.openButtonRows = [];
    }

    public $onInit() {
        this.pagesLookup = toIdLookup(this.publication.pages);
        this.articlesLookup = toIdLookup(this.publication.articles);
        this.advertsLookup = toIdLookup(this.adverts);

        this.presentUsers = this.session.getPresentUsers();

        this.session.subscribeToDisposition({
            pubId: this.publication.id,
            onPage: (data) => {
                const {entity, action} = data;
                switch (action) {
                    case 'CREATE':
                        this.publication.pages.push(entity);
                        this.pagesLookup[entity.id] = entity;
                        break;
                    case 'UPDATE':
                        this.publication.pages.splice(
                            this.publication.pages.findIndex((p) => p.id === entity.id), 1, entity);
                        this.pagesLookup[entity.id] = entity;
                        break;
                    case 'DELETE':
                        this.publication.pages.splice(this.publication.pages.findIndex((p) => p.id === entity.id), 1);
                        delete this.pagesLookup[entity.id];
                }
            },
            onArticle: (data) => {
                const {entity, action} = data;
                switch (action) {
                    case 'CREATE':
                        this.publication.articles.push(entity);
                        this.articlesLookup[entity.id] = entity;
                        break;
                    case 'UPDATE':
                        this.publication.articles.splice(
                            this.publication.articles.findIndex((a) => a.id === entity.id), 1, entity);
                        this.articlesLookup[entity.id] = entity;
                        break;
                }
            },
            onAdvert: (data) => {
                const {entity, action} = data;
                switch (action) {
                    case 'CREATE':
                        this.adverts.push(entity);
                        this.advertsLookup[entity.id] = entity;
                        break;
                    case 'UPDATE':
                        this.adverts.splice(this.adverts.findIndex((a) => a.id === entity.id), 1, entity);
                        this.advertsLookup[entity.id] = entity;
                        break;
                }
            },
            onPageOrder: (data) => this.pageOrder.order = data.entity.order,
            onPageContent: (data) => {
                const { page_id, articles, adverts } = data.entity;
                this.pagesLookup[page_id].articles = articles;
                this.pagesLookup[page_id].adverts = adverts;
            },
            after: () => {
                this.$timeout(() => this.$scope.$apply());
            },
        });

        // this.sortableOptions = uiSortableMultiSelectionMethods.extendOptions({
        //     helper: uiSortableMultiSelectionMethods.helper,
        //     axis: 'y',
        //     handle: '.handle',
        //     placeholder: 'd-placeholder',
        //     start: (e, ui) => ui.placeholder[0].style.height = nodeHeight(ui.helper[0]),
        //     stop: () => pageResource.updatePageOrder({}, this.pageOrder)
        // });

        this.updateDispSize();
        // To recalculate disp table when resizing the screen
        angular.element(this.$window).bind('resize', () => {
            this.updateDispSize();
            this.$timeout(() => this.$scope.$apply());
        });

        this.$scope.$on('$destroy', () => {
            angular.element(this.$window).unbind('resize');
        });
    }

    public toggleButtonRow(articleScope: ArticleScope) {
        if (articleScope.showButtonRow) {
            articleScope.showButtonRow = false;
            this.openButtonRows.splice(this.openButtonRows.indexOf(articleScope), 1);
        } else {
            articleScope.showButtonRow = true;
            this.openButtonRows.push(articleScope);
        }
    }

    public hideAllButtonRows() {
        this.openButtonRows.forEach((scope) => scope.showButtonRow = false);
        this.openButtonRows = [];
    }

    public applyColumnWidth(column: string, extra = {}) {
        return {...extra, ...this.columnWidths[column]};
    }

    public newPages(newPageAt: number, numNewPages: number) {
        this.loading = true;
        this.pageResource.saveMultipleEmpty({
            publicationId: this.publication.id,
            afterPage: newPageAt,
            numNewPages,
        }, (pages) => {
            pages.forEach((p) => {
                this.pagesLookup[p.id] = p;
                this.publication.pages.push(p);
            });
            this.pageOrder.order.splice(newPageAt, 0, ...pages.map((p) => p.id));
            this.loading = false;
        });
    }

    public updatePageMeta(page: Page) {
        this.loading = true;
        this.pageResource.updateMeta({}, page, () => { this.loading = false; });
    }

    public editPage(scope: PageScope) {
        scope.editPage = true;
        scope.newAdverts = scope.page.adverts.map((aid) => this.advertsLookup[aid]);
        scope.newArticles = scope.page.articles.map((aid) => this.articlesLookup[aid]);
    }

    public submitPage(scope: PageScope) {
        scope.page.articles = scope.newArticles.map((a) => a.id);

        scope.page.adverts = scope.newAdverts.map((a) => a.id);

        this.loading = true;
        this.pageResource.updateContent(
            {pageid: scope.page.id},
            {
                publication_id: this.publication.id,
                page_id: scope.page.id,
                articles: scope.page.articles,
                adverts: scope.page.adverts,
            },
            () => { this.loading = false; },
        );
        scope.editPage = false;
    }

    public deletePage(page: Page) {
        if (confirm('Er du sikker på at du vil slette denne siden?')) {
            this.publication.pages.splice(this.publication.pages.indexOf(page), 1);
            this.pageOrder.order.splice(this.pageOrder.order.indexOf(page.id), 1);
            delete this.pagesLookup[page.id];
            this.loading = true;
            this.pageResource.delete({pageid: page.id}, () => this.loading = false);
        }
    }

    public submitArticleField(article: Article, field: string, value: any) {
        article[field] = value;
        this.updateArticle(article);
    }

    public submitAdvertComment(advert: Advert, value: string) {
        advert.comment = value;
        this.advertResource.updateComment({id: advert.id}, JSON.stringify(value));
    }

    public createArticle(page: Page) {
        this.openNewArticleModal({
            persons: [],
            publication: this.publication,
            publications: [this.publication],
            sections: [],
            types: [],
        }).then((article) => {
            this.articlesLookup[article.id] = article;
            this.publication.articles.push(article);
            page.articles.push(article.id);
        });
    }

    public updateArticle(article: Article) {
        this.articleResource.updateStatus({id: article.id}, article);
    }

    public createAdvert(page: Page) {
        this.openNewAdvertModal()
            .then((advert) => {
                this.advertsLookup[advert.id] = advert;
                this.adverts.push(advert);
                page.adverts.push(advert.id);
            });
    }

    // public showHelp(){
    //     $templateRequest('partials/templates/help/dispHelp.html').then(template => {
    //         MessageModal.info(template);
    //     });
    // }

    public updateDispSize() {
        const elementWidth = document.getElementById('disposition').offsetWidth;
        const windowWidth = this.$window.innerWidth;
        const {columnWidths, articleWidth, dispWidth} = this.getDispWidth(elementWidth, windowWidth);
        this.columnWidths = columnWidths;
        this.articleWidth = articleWidth;
        this.dispWidth = dispWidth;
    }
}
