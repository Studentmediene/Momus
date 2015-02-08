/*
 * Copyright 2014 Studentmediene i Trondheim AS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

'use strict';

angular.module('momusApp.services').
    value('noteParserRules',
    {
        tags: {
            br: {},
            p: {},
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
                    target: "_blank"
                },
                check_attributes: {
                    href: "url" // important to avoid XSS
                }
            }
        }
    }
);