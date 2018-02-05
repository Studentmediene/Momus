'use strict';

angular.module('momusApp.services')
    .service('WebSocketService', function($stomp, Person) {
        var actions = {
            saveArticle: 'SAVE_ARTICLE',
            updateArticle: 'UPDATE_ARTICLE',
            savePage: 'SAVE_PAGE',
            deletePage: 'DELETE_PAGE',
            updatePageMetadata: 'UPDATE_PAGE_METADATA',
            updatePagenr: 'UPDATE_PAGE_NR'
        };

        const userAction = {
            alive: 'ALIVE',
        };

        const person = Person.me();

        function sendChange(pubId, pageId, articleId, action, editedField) {
            editedField = editedField || "";
            var change = {action: action, page_id: pageId, article_id: articleId, edited_field: editedField, date: new Date()};
            $stomp.send('/ws/disposition/' + pubId + '/change', change);
            return change;
        }


        function sendUserAction(pubId, action) {
            const userEvent = {user_action: action, userid: person.id};
            $stomp.send('/ws/disposition/' + pubId + '/user', userEvent);
        }

        return {
            subscribe: function(pubId, callback, userActionCallback) {
                $stomp.connect('/api/ws/disposition').then(function(frame) {
                    $stomp.subscribe('/ws/disposition/' + pubId + '/changed', callback);
                    $stomp.subscribe('/ws/disposition/' + pubId + '/users', userActionCallback);
                    sendUserAction(pubId, userAction.alive);
                });
            },
            disconnect: function() {
                $stomp.disconnect();
            },
            pageSaved: function(pubId, pageId) {
                return sendChange(pubId, pageId, -1, actions.savePage);
            },
            pageDeleted: function(pubId, pageId) {
                return sendChange(pubId, pageId, -1, actions.deletePage);          
            },
            pageNrUpdated: function(pubId, pageId) {
                return sendChange(pubId, pageId, -1, actions.updatePagenr, 'page_nr');              
            },
            pageMetadataUpdated: function(pubId, pageId, editedField) {
                return sendChange(pubId, pageId, -1,  actions.updatePageMetadata, editedField);
            },
            articleSaved: function(pubId, pageId, articleId) {
                return sendChange(pubId, pageId, articleId, actions.saveArticle);
            },
            articleUpdated: function(pubId, articleId, editedField) {
                return sendChange(pubId, -1, articleId, actions.updateArticle, editedField);
            },
            actions: actions,
            userAction: userAction,
            sendUserAction: sendUserAction
        };
    });