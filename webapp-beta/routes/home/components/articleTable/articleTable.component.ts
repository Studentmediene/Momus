import * as angular from 'angular';

export default angular.module('momusApp.routes.home.articleTable', [])
    .component('articleTable', {
        bindings: {
            articles: '<',
        },
        template: // html
        `
        <table class="uk-table uk-table-striped uk-table-small">
            <thead>
                <tr>
                    <th>Navn</th>
                    <th>Redaksjon</th>
                    <th>Status</th>
                    <th>Utgave</th>
                </tr>
            </thead>
            <tbody>
                <tr ng-repeat="article in vm.articles">
                    <td>
                        <a ui-sref="article.details({id: article.id})">{{ article.name }}</a>
                        <span title="Antall tegn"> ({{ article.content_length }})</span>
                    </td>
                    <td ng-style="{'background-color': article.section.color}">
                        {{ article.section.name }}
                    </td>
                    <td ng-style="{'background-color': article.status.color}">
                        {{ article.status.name }}
                    </td>
                    <td>{{ article.publication.name }}</td>
                </tr>
            </tbody>
        </table>
        `,
        controllerAs: 'vm',
    });
