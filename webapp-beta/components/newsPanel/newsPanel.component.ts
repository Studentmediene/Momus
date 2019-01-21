import * as angular from 'angular';

import './newsPanel.scss';
import { NewsItem } from 'models/NewsItem';

class NewsPanelCtrl implements angular.IController {
    public news: NewsItem[];

    public pageSize: number = 10;
    public currentPage: number = 0;

    public hasNextPage() {
        const shownNewsAmount = (this.currentPage + 1) * this.pageSize;
        return this.news.length > shownNewsAmount;
    }
}

export default angular.module('momusApp.components.newsPanel', [])
    .component('newsPanel', {
        template: require('./newsPanel.html'),
        controller: NewsPanelCtrl,
        controllerAs: 'vm',
        bindings: {
            news: '<',
            editable: '@',
            onEdit: '&',
        },
    });
