import * as angular from 'angular';
import { ArticleSearchParams } from 'models/ArticleSearchParams';
import { StateService } from '@uirouter/core';
import { OpenNewArticleModal } from 'components/newArticleModal/newArticleModal.component';
import { Publication } from 'models/Publication';
import { Person } from 'models/Person';
import { Section } from 'models/Section';
import { ArticleType, Article } from 'models/Article';

/* @ngInject */
export default class ArticleSearchCtrl implements angular.IController {
    public searchParams: ArticleSearchParams;
    public articleSortType: string = '[publication.release_date,section.name]';
    public articleSortReverse: boolean = false;

    public results: Article[];
    public activePublication: Publication;
    public persons: Person[];
    public publications: Publication[];
    public sections: Section[];
    public types: ArticleType[];

    public hasNextPage: boolean;

    private $scope: angular.IScope;
    private $state: StateService;

    private openNewArticleModal: OpenNewArticleModal;

    constructor(
        $scope: angular.IScope,
        $state: StateService,
        openNewArticleModal: OpenNewArticleModal,
    ) {
        this.$scope = $scope;
        this.$state = $state;

        this.openNewArticleModal = openNewArticleModal;
    }

    public $onInit() {
        this.results.$promise.then(() => {
            this.hasNextPage = this.results.length > this.searchParams.page_size;
        });
    }

    public search(pageDelta: number) {
        if (pageDelta > 0) {
            this.searchParams.page_number = this.searchParams.page_number + pageDelta;
        }
        this.$state.go('.', this.searchParams.stringify(), {inherit: false});
    }

    public sortSearch(type: string, switchDir: boolean) {
        if (this.articleSortType !== type) {
            this.articleSortReverse = switchDir;
            this.articleSortType = type;
        } else {
            this.articleSortReverse = !this.articleSortReverse;
        }

        this.$scope.$apply();
    }

    public createArticle() {
        this.openNewArticleModal({
            publication: this.activePublication,
            publications: this.publications,
            persons: this.persons,
            sections: this.sections,
            types: this.types,
        })
            .then((article) => this.$state.go('article.single', {id: article.id }));
    }

    public hasJournalist(article: Article) {
        return article.journalists.length > 0 || (article.external_author != null && article.external_author !== '');
    }

    public hasPhotographer(article: Article) {
        return article.photographers.length > 0
            || (article.external_photographer != null && article.external_photographer !== '');
    }

    public hasGraphics(article: Article) {
        return article.graphics.length > 0;
    }

    // public showHelp() {
    //     $templateRequest("/assets/partials/templates/help/searchHelp.html").then(function(template){
    //         MessageModal.info(template);
    //     });
    // }
}
