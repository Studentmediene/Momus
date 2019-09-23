const RECENT_ARTICLES_STORE = 'recentlyViewed';
const RECENT_ARTICLES_LENGTH = 5;

const USE_BETA_STORE = 'useBeta';

/* @ngInject */
export default class CookieService {
    private $cookies: ng.cookies.ICookiesService;
    constructor($cookies: ng.cookies.ICookiesService) {
        this.$cookies = $cookies;
    }

    public addToRecentArticles(articleId: number) {
        const recentlyViewed = this.getRecentArticles();

        // If article already in list, remove it
        if (recentlyViewed.indexOf(articleId) !== -1) {
            const index = recentlyViewed.indexOf(articleId);
            recentlyViewed.splice(index, 1);
        }

        // Add it to the front
        recentlyViewed.splice(0, 0, articleId);

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

    public getUseBeta() {
        return this.$cookies.get(USE_BETA_STORE) === 'true';
    }

    public setUseBeta(useBeta: boolean) {
        this.$cookies.put(USE_BETA_STORE, useBeta.toString());
    }

    private updateRecentArticlesInStore(newStore: number[]) {
        this.$cookies.putObject(RECENT_ARTICLES_STORE, newStore);
    }
}
