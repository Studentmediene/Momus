'use strict';

module.exports = function (grunt) {
    require('load-grunt-tasks')(grunt);
    require('time-grunt')(grunt);

    var rootPath = 'src/main/webapp',
        app      = 'src/main/webapp/app',
        dist     = 'src/main/webapp/dist';

    grunt.initConfig({
        clean: {
            dist: {
                files: [{
                    dot: true,
                    src: [
                        dist + '/*'
                    ]
                }]
            }
        },
        jshint: {
            options: {
            },
            all: [
                'Gruntfile.js',
                'src/main/webapp/scripts/{,*/}*.js'
            ]
        },
        rev: {
            dist: {
                files: {
                    src: [
                        dist + '/scripts/{,*/}*.js',
                        dist + '/styles/{,*/}*.css',
                        dist + '/fonts/*'
                    ]
                }
            }
        },
        useminPrepare: {
            html: app + '/index.html',
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
                        'css/*',
                        '*.*'
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
                cwd: app + 'styles',
                dest: '.tmp/styles/',
                src: '{,*/}*.css'
            }
        },
        ngmin: {
            dist: {
                files: [{
                    expand: true,
                    cwd: dist + '/scripts',
                    src: '*.js',
                    dest: dist + '/scripts'
                }]
            }
        },
        uglify: {
            dist: {
                files: {
                    'src/main/webapp/dist/scripts/scripts.js': [
                        dist + '/scripts/scripts.js'
                    ]
                }
            }
        }
    });


//    grunt.registerTask('test', [
//        'clean:server',
//        'concurrent:test',
//        'autoprefixer',
//        'connect:test'
//    ]);

    grunt.registerTask('build', [
        'clean:dist',
        'useminPrepare',
        'copy:dist',
        'ngmin',
        'uglify',
        'rev',
        'usemin'
    ]);

    grunt.registerTask('default', [
        'build'
    ]);
};
