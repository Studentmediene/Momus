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

angular.module('momusApp.services')
    .service('ClipService', function ($http, $timeout) {
        return {
            loadClippy: function (action, message) {
                clippy.load('Clippy', function (agent) {
                    agent.show();
                    if (message) {
                        agent[action](message);
                    } else {
                        agent.play(action);
                    }
                    $timeout(function () {
                        agent.hide();
                    }, 10000)
                });
            },
            playAnimation: function (animation, chance) {
                if (this.getRand() <= chance) {
                    this.loadClippy(animation);
                }
            },
            loadWordClippy: function (article, chance) {
                var message = this.countWords(article.content);
                if (this.getRand() <= chance){
                    if(message) {
                        this.loadClippy("speak", message);
                    } else if(article.content_length > 11000){
                        this.loadClippy("speak", "Skal du skrive en hel bok, eller?");
                    }
                }
            },
            countWords : function(text){
                if(this.countWord(text, "øl") > 2){
                    return "Øl"
                } else if(this.countWord(text, "Samfunnet") > 0){
                    return "Samfundet";
                } else {
                    return false;
                }
            },
            countWord : function (text, word){
                return (text.toLowerCase().match(new RegExp("\s[" + word.toLowerCase() + "]\s", "g")) || []).length;
            },
            getRand : function(){
                return Math.floor((Math.random() * 1000) + 1);
            }
        };
    }
);