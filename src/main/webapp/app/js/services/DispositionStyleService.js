'use strict';

angular.module('momusApp.services')
.service('DispositionStyleService', function () {
    const columnWidthTemplates = {
        page_nr:
            {scope: 'page', width: 20},
        dropdown:
            {scope: 'article', width: 25},
        name: {
            scope: 'article',
            part: 0.25,
            min: 100,
            calculated: 100,
        },
        section: {scope: 'article', width: 100},
        journalists: {
            scope: 'article',
            part: 0.14,
            min: 80,
            calculated: 80
        },
        photographers: {
            scope: 'article',
            part: 0.13,
            min: 80,
            calculated: 80
        },
        graphics: {
            scope: 'article',
            part: 0.13,
            min: 80,
            calculated: 80
        },
        status: {scope: 'article', width: 120},
        review: {scope: 'article', width: 100},
        photo_status: {
            scope: 'article',
            part: 0.15,
            min: 100,
            calculated: 100
        },
        comment: {
            scope: 'article',
            part: 0.2,
            min: 120,
            calculated: 120
        },
        layout: {scope: 'page', width: 80},
        done: {scope: 'page', width: 30},
        edit: {scope: 'page', width: 30},
        delete: {scope: 'page', width: 30}
    };

    // Get the width of the entire disposition and article table that is constant
    const [constDispWidth, constArticleWidth] = Object.keys(columnWidthTemplates)
        .filter(key => columnWidthTemplates[key].width)
        .reduce(([dispSize, articleSize],key) => {
            const colWidth = columnWidthTemplates[key].width;
            return [
                dispSize + colWidth,
                articleSize + (columnWidthTemplates[key].scope === 'article' ? colWidth : 0)
            ];
        }, [0, 0]
    );

    return {
        calcDispSize: function (elementWidth, windowWidth){
            const widthLeft = elementWidth - constDispWidth;

            let articleWidth = constArticleWidth;
            let dispWidth = constDispWidth;
            Object.keys(columnWidthTemplates)
                .filter(key => columnWidthTemplates[key].part)
                .forEach(key => {
                    const column = columnWidthTemplates[key];
                    const width = windowWidth > 992 ? Math.floor(column.part*widthLeft) : column.min;
                    column.calculated = width;
                    if(column.scope === 'article') {
                        articleWidth += width;
                    }
                    dispWidth += width;
                });

            const columnWidths = {};
            Object.keys(columnWidthTemplates)
                .forEach(key => {
                    const col = columnWidthTemplates[key];
                    const columnWidth = (col.width | col.calculated) + 'px';
                    columnWidths[key] = {
                        minWidth: columnWidth,
                        width: columnWidth,
                        maxWidth: columnWidth,
                    };
                });

            return {
                columnWidths: columnWidths,
                articleWidth: articleWidth,
                dispWidth: dispWidth + 1
            };
        }
    };
});
