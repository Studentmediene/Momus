var wysihtml5ParserRules = {
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
};

var noteParserRules = {
    tags: {
        strong: {
            rename_tag: "b"
        },
        b: {},
        i: {},
        ul: {},
        br: {},
        p: {},
        span: {},
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
};