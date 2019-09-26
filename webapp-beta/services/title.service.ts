import { RootScope } from 'app.types';

/* @ngInject */
export default class TitleService {
    constructor(private $rootScope: RootScope) {}

    public setTitle(title?: string) {
        this.$rootScope.pageTitle = title != null ? `${title} - Momus` : 'Momus';
    }
}
