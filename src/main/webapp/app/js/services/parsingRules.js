'use strict';

angular.module('momusApp.services').
    value('articleParsingRules',
    {
        tags: {
            strong: {
                rename_tag: "b"
            },
            b: {},
            i: {},
            em: {},
            br: {},
            p: {},
            div: {},
            span: {},
            ul: {},
            ol: {},
            li: {},
            a: {
                set_attributes: {
                    target: "_blank",
                    rel: "nofollow"
                },
                check_attributes: {
                    href: "url" // important to avoid XSS
                }
            }
        }
    }
).value('noteParserRules',
    {
        tags: {
            br: {},
            p: {},
            span: {},

            strong: {
                rename_tag: "b"
            },
            b: {},
            i: {},
            del: {
                rename_tag: "strike"
            },
            strike: {},
            u: {},

            ul: {},
            ol: {},
            li: {},
            a: {
                set_attributes: {
                    target: "_blank",
                    rel: "nofollow"
                },
                check_attributes: {
                    href: "url" // important to avoid XSS
                }
            }
        }
    }
);