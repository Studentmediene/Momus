import * as angular from 'angular';

import inlineEdit from './inlineEdit/inlineEdit.component';
import panel from './panel/panel.component';
import personAvatar from './personAvatar/personAvatar.component';
import personWidget from './personWidget/personWidget.component';
import statusWidget from './statusWidget/statusWidget.component';
import booleanWidget from './booleanWidget/booleanWidget.component';
import textwrapper from './textwrapper/textwrapper.component';
import multiselectDropdown from './multiselectDropdown/multiselectDropdown.component';
import editableAlert from './editableAlert/editableAlert.component';
import editableField from './editableField/editableField.component';

export default angular
    .module('momusApp.components', [
        personAvatar.name,
        personWidget.name,
        statusWidget.name,
        booleanWidget.name,
        inlineEdit.name,
        textwrapper.name,
        panel.name,
        multiselectDropdown.name,
        editableAlert.name,
        editableField.name,
    ]);
