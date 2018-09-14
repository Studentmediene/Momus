import * as angular from 'angular';
import { NewsItem } from 'models/NewsItem';
import { Env } from 'app.types';
import { Person } from 'models/Person';
import { NewsItemResource } from 'resources/newsItem.resource';

/* @ngInject */
class AdminCtrl implements angular.IController {
    public news: NewsItem[];

    public isSaving: boolean = false;
    public editing: NewsItem;

    public newsForm: angular.INgModelController;

    private newsItemResource: NewsItemResource;
    private loggedInUser: Person;

    constructor(loggedInUser: Person, newsItemResource: NewsItemResource) {
        this.loggedInUser = loggedInUser;
        this.newsItemResource = newsItemResource;
    }

    public editNews(item: NewsItem) {
        // always work on a copy
        this.editing = angular.copy(item);
        // clear form errors
        this.newsForm.$setPristine();
    }

    public saveEditedNews() {
        this.isSaving = true;
        if (this.editing.id == null) { // no id means it's a new one
            this.editing.author = this.loggedInUser;
            const newsItem = this.newsItemResource.save({}, this.editing, () => {
                this.news.push(newsItem);
                this.editNews(newsItem);
                this.isSaving = false;
            });
        } else { // it's an old one
            const updatedIndex = this.news.findIndex((news) => news.id === this.editing.id);
            this.editing.$update({}, (updated: NewsItem) => {
                this.news[updatedIndex] = updated;
                this.editNews(updated);
                this.isSaving = false;
            });
        }
    }

}

export default angular.module('momusApp.routes.admin.adminPage', [])
    .component('adminPage', {
        bindings: {
            user: '<',
            news: '<',
        },
        template: require('./admin.html'),
        controller: AdminCtrl,
        controllerAs: 'vm',
    });
