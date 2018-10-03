import * as angular from 'angular';
import { PageResource } from 'resources/page.resource';
import { AdvertResource } from 'resources/advert.resource';
import { Publication } from 'models/Publication';
import { PageOrder, Page } from 'models/Page';
import { Advert } from 'models/Advert';
import { Article } from 'models/Article';
import { Session } from 'services/session.service';
import { Person } from 'models/Person';
import { OpenNewArticleModal } from 'components/newArticleModal/newArticleModal.component';
import { toIdLookup } from 'utils';
import { OpenNewAdvertModal } from 'components/newAdvertModal/newAdvertModal.component';

import getDispWidth, { ColumnWidths } from './getColumnWidths';

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
    private advertResource: AdvertResource;

    private openNewArticleModal: OpenNewArticleModal;
    private openNewAdvertModal: OpenNewAdvertModal;

    private $scope: angular.IScope;
    private $timeout: angular.ITimeoutService;
    private $window: angular.IWindowService;

    constructor(
        $scope: angular.IScope,
        $timeout: angular.ITimeoutService,
        $window: angular.IWindowService,
        // MessageModal,
        pageResource: PageResource,
        advertResource: AdvertResource,
        openNewArticleModal: OpenNewArticleModal,
        openNewAdvertModal: OpenNewAdvertModal,
    ) {
        this.pageResource = pageResource;
        this.advertResource = advertResource;
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

    public deletePage(page: Page) {
        if (confirm('Er du sikker på at du vil slette denne siden?')) {
            this.publication.pages.splice(this.publication.pages.indexOf(page), 1);
            this.pageOrder.order.splice(this.pageOrder.order.indexOf(page.id), 1);
            delete this.pagesLookup[page.id];
            this.loading = true;
            this.pageResource.delete({pageid: page.id}, () => this.loading = false);
        }
    }

    public submitAdvertComment(advert: Advert, value: string) {
        advert.comment = value;
        this.advertResource.updateComment({id: advert.id}, JSON.stringify(value));
    }

    public updateDispSize() {
        const elementWidth = document.getElementById('disposition').offsetWidth;
        const windowWidth = this.$window.innerWidth;
        const {columnWidths, articleWidth, dispWidth} = getDispWidth(elementWidth, windowWidth);
        this.columnWidths = columnWidths;
        this.articleWidth = articleWidth;
        this.dispWidth = dispWidth;
    }
}
