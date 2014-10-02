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

// # Globbing
// for performance reasons we're only matching one level down:
// 'test/spec/{,*/}*.js'
// use this if you want to recursively match all subfolders:
// 'test/spec/**/*.js'


module.exports = function (grunt) {
    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    var dist = 'src/main/webapp/dist';
    var app = 'src/main/webapp/app';

    grunt.initConfig({
        clean: {
            dist: {
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp/*',
                            dist + '/*'
                        ]
                    }
                ]
            },
            tmp: {
                files: [
                    {
                        dot: true,
                        src: [
                            '.tmp'
                        ]
                    }
                ]
            }
        },
        jshint: {
            options: {
                globals: {
                    alert: true,
                    confirm: true,
                    window: true,
                    angular: true,
                    module: true,
                    console: true
                },
                globalstrict: true
            },
            all: [
                app + '/js/**/*.js'
            ]
        },
        rev: {
            dist: {
                files: {
                    src: [
                        dist + '/js/{,*/}*.js',
                        dist + '/css/!(editor).css' // all css-files except the editor.css
                    ]
                }
            }
        },
        useminPrepare: {
            html: app + '/index.html',
//            css: app + 'css/**',
            options: {
                dest: dist
            }
        },
        usemin: {
            html: [dist + '/{,*/}*.html'],
            css: [dist + '/css/{,*/}*.css'],
            options: {
                dirs: [dist]
            }
        },

        // Put files not handled in other tasks here that should be copied to dist
        copy: {
            dist: {
                files: [
                    {
                        expand: true,
                        dot: true,
                        cwd: app,
                        dest: dist,
                        src: [
                            '*.{ico,png,txt}',
                            'images/**',
                            'partials/**',
                            'index.html'
                        ]
                    }
                ]
            },
            fonts: {
                files: [
                    {
                        expand: true,
                        cwd: app + '/libs/components-font-awesome/fonts/',
                        src: ['*'],
                        dest: dist + '/fonts/'

                    },
                    {
                        expand: true,
                        cwd: app + '/libs/bootstrap/dist/fonts/',
                        src: ['*'],
                        dest: dist + '/fonts/'

                    }
                ]
            },
            favicon: {
                files: [
                    {
                        expand: true,
                        cwd: 'src/main/webapp/',
                        src: ['favicon.ico'],
                        dest: dist + '/'
                    }
                ]
            },
            select2images: { // needed because select2's css has hardcoded path to some images
                files: [
                    {
                        expand: true,
                        cwd: app + '/libs/select2',
                        src: ['*.png', '*.gif'],
                        dest: dist + '/css'
                    }
                ]
            },
            editorCSS: {
                files: [
                    {
                        expand: true,
                        cwd: app + '/css/',
                        src: ['editor.css'],
                        dest: dist + '/css'
                    }
                ]
            },
            zeroClipboardSWF: {
                files: [
                    {
                        expand: true,
                        cwd: app + '/libs/zeroclipboard/dist',
                        src: ['ZeroClipboard.swf'],
                        dest: dist + '/js'
                    }
                ]
            }
        },
        ngmin: {
            dist: {
                files: [
                    {
                        expand: true,
                        cwd: '.tmp/concat/js',
                        src: 'main.js',
                        dest: '.tmp/concat/js'
                    }
                ]
            }
        },
        karma: {
            unit: {
                configFile: 'src/main/webapp/test/config/karma.conf.js',
                singleRun: true
            }
        }
    });

    grunt.registerTask('test', [
        'clean:server',
        'concurrent:test',
        'autoprefixer',
        'connect:test'
    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'useminPrepare',
        'concat',
        'copy',
        'ngmin',
        'cssmin',
        'uglify',
        'rev',
        'usemin',
        'clean:tmp'
    ]);

    grunt.registerTask('default', [
        'build'
    ]);
};
