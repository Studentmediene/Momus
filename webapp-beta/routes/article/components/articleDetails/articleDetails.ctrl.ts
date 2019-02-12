import * as angular from 'angular';
import { TransitionService, StateService } from '@uirouter/core';

import { ArticleResource } from 'resources/article.resource';
import { Article, ArticleType } from 'models/Article';
import { ArticleStatus, ReviewStatus } from 'models/Statuses';
import { Person } from 'models/Person';
import { Publication } from 'models/Publication';
import { Section } from 'models/Section';

/* @ngInject */
export default class ArticleDetailsCtrl implements angular.IController {
    public metaEditMode: boolean = false;
    public photoTypes: object[] = [{value: false, name: 'Foto'}, {value: true, name: 'Illustrasjon'}];

    public article: Article;
    public articleContent: string;

    public publications: Publication[];

    public savingNote: boolean;
    public uneditedNote: string;

    private articleResource: ArticleResource;

    constructor(
        $state: StateService,
        $transitions: TransitionService,
        $templateRequest: angular.ITemplateRequestService,
        articleResource: ArticleResource,
    ) {
        this.articleResource = articleResource;

        $transitions.onBefore({from: $state.current.name }, (transition) => {
            if (this.promptCondition()) {
                return confirm('Er du sikker på at du vil forlate siden? Det finnes ulagrede endringer.');
            }
            return true;
        });
    }

    public $onInit() {
        this.uneditedNote = this.article.note;

        window.onbeforeunload = () => {
            if (this.promptCondition()) {
                return true;
            }
        };
    }

    public $onDestroy() {
        window.onbeforeunload = undefined;
    }

    public onStatusChange(newStatus: ArticleStatus) {
        this.article.status = newStatus;
        this.saveStatus();
    }

    public onReviewStatusChange(newReviewStatus: ReviewStatus) {
        this.article.review = newReviewStatus;
        this.saveStatus();
    }

    public onQuoteCheckStatusChange(newQuoteCheckStatus: boolean) {
        this.article.quote_check_status = newQuoteCheckStatus;
        this.saveStatus();
    }

    public onCommentSave(newComment: string) {
        this.article.comment = newComment;
        this.saveMetadata();
    }

    public onJournalistsSave(journalists: Person[]) {
        this.article.journalists = journalists;
        this.saveMetadata();
    }

    public onExternalAuthorSave(external: string) {
        this.article.external_author = external;
        this.saveMetadata();
    }

    public onPhotographersSave(photographers: Person[]) {
        this.article.photographers = photographers;
        this.saveMetadata();
    }

    public onGraphicsSave(graphics: Person[]) {
        this.article.graphics = graphics;
        this.saveMetadata();
    }

    public onExternalPhotographerSave(external: string) {
        this.article.external_photographer = external;
        this.saveMetadata();
    }

    public onImageTextSave(text: string) {
        this.article.image_text = text;
        this.saveMetadata();
    }

    public onSectionSave(section: Section) {
        this.article.section = section;
        this.saveMetadata();
    }

    public onNameSave(name: string) {
        this.article.name = name;
        this.saveMetadata();
    }

    public onTypeSave(type: ArticleType) {
        this.article.type = type;
        this.saveMetadata();
    }

    public onPublicationSave(publication: Publication) {
        this.article.publication = publication;
        this.saveMetadata();
    }

    public saveMetadata() {
        this.articleResource.updateMetadata({ id: this.article.id }, this.article);
    }

    public saveStatus() {
        this.articleResource.updateStatus({ id: this.article.id }, this.article);
    }

    public archiveArticle() {
        if (confirm('Er du sikker på at du vil slette artikkelen?')) {
            this.article.archived = true;
            this.articleResource.archive({id: this.article.id});
        }
    }

    public restoreArticle() {
        this.article.archived = false;
        this.articleResource.restore({id: this.article.id});
    }

    public showHelp() {
        // $templateRequest('/assets/partials/templates/help/articleHelp.html').then(function(template) {
        //     MessageModal.info(template);
        // });
    }

    private promptCondition() {
        return this.metaEditMode === true || this.uneditedNote !== this.article.note;
    }
}
