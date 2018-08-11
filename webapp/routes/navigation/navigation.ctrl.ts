import { NavItem } from './navigation.route';
import { Person } from '../../models/Person';

import './navigation.scss';

/* @ngInject */
export default class NavigationCtrl implements angular.IController {
    public user: Person;
    public navItems: NavItem[];
}
