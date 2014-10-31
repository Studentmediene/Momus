'use strict';

angular.module('momusApp.services')
    .service('SavedSearchService', function ($http, $log) {
        return {
            getSavedSearches: function () {
                return $http.get('/api/savedsearch/get');
            },
            saveSearch: function (name, url, desc) {
                return $http.post('/api/savedsearch/new', {name:name, description: JSON.stringify(desc), url:url, owner:0});
            },
            deleteSearch: function(id) {
                return $http.delete('/api/savedsearch/del/'+id)
            },
            getDesc: function(search, persons, pubs, statuses, sections) {
                var obj = {};
                if(search.free){
                    obj.free = search.free;
                }
                if(search.publication){
                    for(var i=0;i<pubs.length;i++){
                        if(pubs[i].id == search.publication){
                            obj.publication = pubs[i].name;
                            break;
                        }
                    }
                }
                if(search.persons.length >0){
                    obj.persons = [];
                    for(var j=0;j<search.persons.length;j++){
                        for(var k=0;k<persons.length;k++){
                            if(persons[k].id == search.persons[j]){
                                $log.log(persons[k].full_name);
                                obj.persons.push(persons[k]);
                            }
                        }
                    }
                }
                if(search.status){
                    for(var i=0;i<statuses.length;i++){
                        if(statuses[i].id == search.status){
                            obj.status = statuses[i].name;
                            break;
                        }
                    }
                }
                if(search.section){
                    for(var i=0;i<sections.length;i++){
                        if(sections[i].id == search.section){
                            obj.section = sections[i].name;
                            break;
                        }
                    }
                }
                return obj;
            }
        };
    });