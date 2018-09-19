import * as angular from 'angular';
import { ISCEService } from 'angular';
import { RevisionDiff } from 'models/ArticleRevision';

export default angular
    .module('momusApp.filters', [])
    .filter('trustHtml', ($sce: ISCEService) => (text: string) => $sce.trustAsHtml(text))
    .filter('diffsToHtml', ($sce: ISCEService) => (diffs: RevisionDiff[], type: 'del' | 'add') =>
        $sce.trustAsHtml(diffs.reduce((html, diff) => {
            if (diff.operation === 'DELETE' && type === 'del') {
                return `${html}<del>${diff.text}</del>`;
            }
            if (diff.operation === 'DELETETAG' && type === 'del') {
                return `${html}${diff.text.slice(0, -1)} class=\"del\">`; // add a del class
            }
            if (diff.operation === 'EQUAL') {
                return `${html}${diff.text}`;
            }
            if (diff.operation === 'INSERT' && type === 'add') {
                return `${html}<ins>${diff.text}</ins>`;
            } else if (diff.operation === 'INSERTTAG' && type === 'add') {
                return `${html}${diff.text.slice(0, -1)} class=\"ins\">`; // add a ins class
            }

            return html;
        }, '')),
    )
    .filter('initials', () => (name: string) =>
        name.trim().split(' ')
            .filter((e, i, a) => i === 0 || i === a.length - 1)
            .map((e) => e[0])
            .join(''),
    )
    .filter('shortname', () => (name: string) =>
        name.trim().split(' ')
            .map((e, i, a) => i === a.length - 1 ? e : `${e[0]}.`)
            .join(' '),
    )
    .filter('startFrom', () => {
        return (list: any[], start: number) => {
            start = +start;
            return list.slice(start);
        };
    });
