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
                files: [{
                    dot: true,
                    src: [
                        '.tmp',
                        dist + '/*'
                    ]
                }]
            },
            tmp : {
                files: [{
                    dot: true,
                    src: [
                        '.tmp'
                    ]
                }]
            }
        },
//        jshint: {
//            options: {
//                jshintrc: '.jshintrc'
//            },
//            all: [
//                'Gruntfile.js',
//                'src/main/webapp/scripts/{,*/}*.js'
//            ]
//        },
//        cssmin: {
//            dist: {
//                files: {
//
//                }
//            }
//        },
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
            css: app + 'css/**',
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

        // Put files not handled in other tasks here
        copy: {
            dist: {
                files: [{
                    expand: true,
                    dot: true,
                    cwd: app,
                    dest: dist,
                    src: [
                        '*.{ico,png,txt}',
                        '.htaccess',
                        'bower_components/**/*',
                        'images/{,*/}*.{gif,webp}',
                        'index.html',
                        'lib/**'
                    ]
                }, {
                    expand: true,
                    cwd: '.tmp/images',
                    dest: dist + '/images',
                    src: [
                        'generated/*'
                    ]
                }]
            },
            styles: {
                expand: true,
                cwd: 'src/main/webapp/app/css',
                dest: '.tmp/css/',
                src: '{,*/}*.css'
            }
        },
        ngmin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: dist + '/js',
                    src: '*.js',
                    dest: dist + '/scripts'
                }]
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
        'copy:styles',
        'concat',
        'copy:dist',
        'ngmin',
        'cssmin',
//        'uglify',
        'rev',
        'usemin'
    ]);

    grunt.registerTask('default', [
        'build'
    ]);
};
