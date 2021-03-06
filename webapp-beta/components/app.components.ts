import * as angular from 'angular';

import inlineEdit from './inlineEdit/inlineEdit.component';
import panel from './panel/panel.component';
import personAvatar from './personAvatar/personAvatar.component';
import personWidget from './personWidget/personWidget.component';
import statusWidget from './statusWidget/statusWidget.component';
import booleanWidget from './booleanWidget/booleanWidget.component';
import textwrapper from './textwrapper/textwrapper.component';
import editableAlert from './editableAlert/editableAlert.component';
import editableField from './editableField/editableField.component';
import newArticleModal from './newArticleModal/newArticleModal.component';
import newAdvertModal from './newAdvertModal/newAdvertModal.component';
import newsPanel from './newsPanel/newsPanel.component';
import messageModal from './messageModal/messageModal.component';
import pageAdder from './pageAdder/pageAdder.component';
import errorMessage from './errorMessage/errorMessage.component';

export default angular
    .module('momusApp.components', [
        personAvatar.name,
        personWidget.name,
        statusWidget.name,
        booleanWidget.name,
        inlineEdit.name,
        textwrapper.name,
        panel.name,
        editableAlert.name,
        editableField.name,
        newArticleModal.name,
        newAdvertModal.name,
        newsPanel.name,
        messageModal.name,
        pageAdder.name,
        errorMessage.name,
    ])
    .component('loadingSpinner', {
        template: '<i class="fas fa-circle-notch fa-4x fa-spin"></i>',
    });
