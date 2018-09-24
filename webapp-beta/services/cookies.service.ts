import { Article } from 'models/Article';

const RECENT_ARTICLES_STORE = 'recentlyViewed';
const RECENT_ARTICLES_LENGTH = 5;

/* @ngInject */
export default class CookieService {
    private $cookies: ng.cookies.ICookiesService;
    constructor($cookies: ng.cookies.ICookiesService) {
        this.$cookies = $cookies;
    }

    public addToRecentArticles(article: Article) {
        const recentlyViewed = this.getRecentArticles();

        // If article already in list, remove it
        if (recentlyViewed.indexOf(article.id) !== -1) {
            const index = recentlyViewed.indexOf(article.id);
            recentlyViewed.splice(index, 1);
        }

        // Add it to the front
        recentlyViewed.splice(0, 0, article.id);

        // Trim to correct length
        const cut = recentlyViewed.filter((_, i) => i < RECENT_ARTICLES_LENGTH);

        this.updateRecentArticlesInStore(cut);
    }

    public getRecentArticles() {
        const store = this.$cookies.getObject(RECENT_ARTICLES_STORE);
        if (store == null) {
            return [];
        }
        return <number[]> store;
    }

    private updateRecentArticlesInStore(newStore: number[]) {
        this.$cookies.putObject(RECENT_ARTICLES_STORE, newStore);
    }
}
