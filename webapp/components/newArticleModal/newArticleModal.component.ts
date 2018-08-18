import * as angular from 'angular';

import { Publication } from '../../models/Publication';
import { Article, ArticleType } from 'models/Article';

import './newArticleModal.scss';
import { ArticleResource } from 'resources/article.resource';
import { Person } from 'models/Person';
import { Section } from 'models/Section';

interface ModalInput {
    publication: Publication;
    publications: Publication[];
    persons: Person[];
    sections: Section[];
    types: ArticleType[];
}

interface ModalScope extends angular.IScope, ModalInput {
    onFinished: (createdArticle: Article) => void;
    onCanceled: () => void;
}

export type OpenNewArticleModalFactory =
    ({ publication, persons }: ModalInput) =>
    Promise<Article>;

/* @ngInject */
function openNewArticleModalFactory(
    $rootScope: angular.IRootScopeService,
    $compile: angular.ICompileService,
): OpenNewArticleModalFactory {
    return ({ persons, publication, publications, sections, types }) => {
        return new Promise((resolve, reject) => {
            const html = `
            <new-article-modal
                publication="publication"
                publications="publications"
                persons="persons"
                sections="sections"
                types="types"
                on-finished="onFinished(article)"
                on-canceled="onCanceled()">
            </new-article-modal>`;
            const scope: ModalScope = <ModalScope> $rootScope.$new(true);
            scope.publication = publication;
            scope.publications = publications;
            scope.persons = persons;
            scope.sections = sections;
            scope.types = types;

            const element = $compile(html)(scope);
            scope.onFinished = (createdArticle) => {
                element.remove();
                resolve(createdArticle);
            };
            scope.onCanceled = () => {
                element.remove();
                reject();
            };

            angular.element(document.body).append(element);
        });
    };
}

/* @ngInject */
class NewArticleModalCtrl implements angular.IController {
    public article: Article;
    public publication: Publication;
    public publications: Publication[];

    public onFinished: ({ article }: { article: Article }) => void;

    private articleResource: ArticleResource;

    constructor(articleResource: ArticleResource) {
        this.articleResource = articleResource;
    }

    public $onInit() {
        this.article = new this.articleResource({
            name: null,
            journalists: [],
            photographers: [],
            comment: '',
            publication: this.publications.find((pub) => pub.id === this.publication.id),
            type: null,
            status: null,
            section: null,
            review: null,
            content: '',
            use_illustration: false,
            external_author: '',
            external_photographer: '',
            quote_check_status: false,
            image_text: '',
            note: '',
            archived: false,
        });
    }

    public create() {
        this.article.$save({}, (article: Article) => {
            this.onFinished({ article });
        });
    }

    public modalClick(evt: MouseEvent) {
        evt.stopPropagation();
    }

    public isArticleValid() {
        return this.article.name != null
            && this.article.name !== ''
            && this.article.type != null
            && this.article.section != null
            && this.article.publication != null;
    }
}

export default angular
    .module('momusApp.components.newArticleModal', [])
    .factory('openNewArticleModal', openNewArticleModalFactory)
    .component('newArticleModal', {
        template: require('./newArticleModal.html'),
        controller: NewArticleModalCtrl,
        controllerAs: 'vm',
        bindings: {
            persons: '<',
            publication: '<',
            publications: '<',
            sections: '<',
            types: '<',
            onFinished: '&',
            onCanceled: '&',
        },
    });
