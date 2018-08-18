import * as angular from 'angular';
import { ArticleSearchParams } from 'models/ArticleSearchParams';
import { StateService } from '@uirouter/core';
import { OpenNewArticleModalFactory } from 'components/newArticleModal/newArticleModal.component';
import { Publication } from 'models/Publication';
import { Person } from 'models/Person';
import { Section } from 'models/Section';
import { ArticleType } from 'models/Article';

/* @ngInject */
export default class ArticleSearchCtrl implements angular.IController {
    public searchParams: ArticleSearchParams;
    public articleSortType: string = '[publication.release_date,section.name]';
    public articleSortReverse: boolean = false;
    public activePublication: Publication;
    public persons: Person[];
    public publications: Publication[];
    public sections: Section[];
    public types: ArticleType[];

    private $scope: angular.IScope;
    private $state: StateService;

    private openNewArticleModal: OpenNewArticleModalFactory;

    constructor(
        $scope: angular.IScope,
        $state: StateService,
        openNewArticleModal: OpenNewArticleModalFactory,
    ) {
        this.$scope = $scope;
        this.$state = $state;

        this.openNewArticleModal = openNewArticleModal;
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
            .then((article) => {
                console.log(article);
        });
    }

    // public showHelp() {
    //     $templateRequest("partials/templates/help/searchHelp.html").then(function(template){
    //         MessageModal.info(template);
    //     });
    // }
}
