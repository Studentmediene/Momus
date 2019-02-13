interface ColumnWidthTemplates {
    [index: string]: {
        scope: string;
        width?: number;
        part?: number;
        min?: number;
        calculated?: number;
    };
}

export interface ColumnWidths {
    [index: string]: {
        minWidth: string;
        width: string;
        maxWidth: string;
    };
}

const columnWidthTemplates: ColumnWidthTemplates = {
    page_nr:
        {scope: 'page', width: 25},
    dropdown:
        {scope: 'article', width: 20},
    name: {
        scope: 'article',
        part: 0.25,
        min: 100,
        calculated: 100,
    },
    section: {scope: 'article', width: 100},
    journalists: {
        scope: 'article',
        part: 0.2,
        min: 80,
        calculated: 80,
    },
    photographers: {
        scope: 'article',
        part: 0.2,
        min: 80,
        calculated: 80,
    },
    status: {scope: 'article', width: 130},
    review: {scope: 'article', width: 100},
    photo_status: {
        scope: 'article',
        part: 0.15,
        min: 100,
        calculated: 100,
    },
    comment: {
        scope: 'article',
        part: 0.2,
        min: 120,
        calculated: 120,
    },
    layout: {scope: 'page', width: 90},
    buttons: {scope: 'page', width: 90},
};

// Get the width of the entire disposition and article table that is constant
const [constDispWidth, constArticleWidth] = Object.keys(columnWidthTemplates)
    .filter((key) => columnWidthTemplates[key].width)
    .reduce(([dispSize, articleSize], key) => {
        const colWidth = columnWidthTemplates[key].width;
        return [
            dispSize + colWidth,
            articleSize + (columnWidthTemplates[key].scope === 'article' ? colWidth : 0),
        ];
    }, [0, 0],
);

export default (elementWidth: number, windowWidth: number) => {
    const widthLeft = elementWidth - constDispWidth;

    let articleWidth = constArticleWidth;
    let dispWidth = constDispWidth;
    Object.keys(columnWidthTemplates)
        .filter((key) => columnWidthTemplates[key].part)
        .forEach((key) => {
            const column = columnWidthTemplates[key];
            const width = windowWidth > 992 ? Math.floor(column.part * widthLeft) : column.min;
            column.calculated = width;
            if (column.scope === 'article') {
                articleWidth += width;
            }
            dispWidth += width;
        });

    const columnWidths: ColumnWidths = {};
    Object.keys(columnWidthTemplates)
        .forEach((key) => {
            const col = columnWidthTemplates[key];
            const columnWidth = (col.width || col.calculated) + 'px';
            columnWidths[key] = {
                minWidth: columnWidth,
                width: columnWidth,
                maxWidth: columnWidth,
            };
        });

    return {
        columnWidths,
        articleWidth,
        dispWidth: dispWidth + 1,
    };
};
