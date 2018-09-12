import * as angular from 'angular';

import inlineEdit from './inlineEdit/inlineEdit.component';
import panel from './panel/panel.component';
import personAvatar from './personAvatar/personAvatar.component';
import personWidget from './personWidget/personWidget.component';
import statusWidget from './statusWidget/statusWidget.component';
import booleanWidget from './booleanWidget/booleanWidget.component';
import textwrapper from './textwrapper/textwrapper.component';
import selectDropdown from './selectDropdown/selectDropdown.component';
import multiselectDropdown from './multiselectDropdown/multiselectDropdown.component';
import editableAlert from './editableAlert/editableAlert.component';
import editableField from './editableField/editableField.component';
import newArticleModal from './newArticleModal/newArticleModal.component';
import newAdvertModal from './newAdvertModal/newAdvertModal.component';
import newsPanel from './newsPanel/newsPanel.component';
import messageModal from './messageModal/messageModal.component';

export default angular
    .module('momusApp.components', [
        personAvatar.name,
        personWidget.name,
        statusWidget.name,
        booleanWidget.name,
        inlineEdit.name,
        textwrapper.name,
        panel.name,
        selectDropdown.name,
        multiselectDropdown.name,
        editableAlert.name,
        editableField.name,
        newArticleModal.name,
        newAdvertModal.name,
        newsPanel.name,
        messageModal.name,
    ]);
