import * as angular from 'angular';

import { Publication } from '../../models/Publication';
import { Article, ArticleType } from 'models/Article';

import './newArticleModal.scss';
import { ArticleResource } from 'resources/article.resource';
import { Person } from 'models/Person';
import { Section } from 'models/Section';
import { OpenModal, ModalInput } from 'services/openModal.factory';

interface ArticleModalInput extends ModalInput {
    publication: Publication;
    publications: Publication[];
    persons: Person[];
    sections: Section[];
    types: ArticleType[];
}

export type OpenNewArticleModal = (input: ArticleModalInput) => Promise<Article>;

/* @ngInject */
class NewArticleModalCtrl implements angular.IController {
    public article: Article;
    public publication: Publication;
    public publications: Publication[];

    public onFinished: ({ value }: { value: Article }) => void;

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
            this.onFinished({ value: article });
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
    .factory('openNewArticleModal', (openModal: OpenModal<ArticleModalInput, Article>) => (
        (inputs: ArticleModalInput) => openModal('newArticleModal', inputs)
    ))
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
