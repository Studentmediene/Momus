import * as angular from 'angular';

import 'angular-drag-and-drop-lists';

import PublicationDispositionCtrl from './publicationDisposition.ctrl';

import dispositionPage from '../dispositionPage/dispositionPage.component';
import dispositionArticle from '../dispositionArticle/dispositionArticle.component';
import dispositionAdvert from '../dispositionAdvert/dispositionAdvert.component';

import './publicationDisposition.scss';

export default angular.module('momusApp.routes.publication.publicationDisposition', [
    'dndLists',
    dispositionPage.name,
    dispositionArticle.name,
    dispositionAdvert.name,
])
    .component('publicationDisposition', {
        bindings: {
            publication: '<',
            pages: '<',
            pageOrder: '<',
            articles: '<',
            adverts: '<',
            articleStatuses: '<',
            reviewStatuses: '<',
            layoutStatuses: '<',
            session: '<',
        },
        controller: PublicationDispositionCtrl,
        controllerAs: 'vm',
        template: require('./publicationDisposition.html'),
    });
