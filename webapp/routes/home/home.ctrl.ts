import { Person } from '../../models/Person';
import { Article } from '../../models/Article';
import { IController } from 'angular';

/* @ngInject */
export default class HomeCtrl implements IController {
    public user: Person;
    public myArticles: Article[];
}
