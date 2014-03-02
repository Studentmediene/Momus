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
                        dist + '/css/{,*/}*.css'
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
            select2images: { // needed because select2's css has hardcoded path to some images
                files: [
                    {
                        expand: true,
                        cwd: app + '/libs/select2',
                        src: ['*.png', '*.gif'],
                        dest: dist + '/css'
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
        'copy:dist',
        'copy:fonts',
        'copy:select2images',
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
