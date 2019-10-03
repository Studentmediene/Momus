import { NewsItem } from '../models/NewsItem';
import { MomResourceFactory } from '../services/momResource.factory';

/* @ngInject */
export default function newsItemResourceFactory(
    momResource: MomResourceFactory<NewsItem>,
): NewsItemResource {
    return momResource(
        '/api/newsitem/:id',
        {
            id: '@id',
        },
        {},
        newsItemRequestTransform,
        newsItemResponseTransform,
    );
}

export interface NewsItemResource extends ng.resource.IResourceClass<NewsItem> {
}

function newsItemResponseTransform(newsItem: NewsItem) {
    if (!newsItem) {
        return newsItem;
    }
    return {
        ...newsItem,
        date: new Date(newsItem.date),
    };
}

function newsItemRequestTransform(newsItem: NewsItem) {
    if (!newsItem || newsItem.date === undefined) {
        return newsItem;
    }
    return {
        ...newsItem,
        date: newsItem.date.toISOString(),
    };
}
