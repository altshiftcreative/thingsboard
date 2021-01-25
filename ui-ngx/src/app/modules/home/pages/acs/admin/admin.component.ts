import { Component, ViewEncapsulation } from '@angular/core';
import { ThemePalette } from '@angular/material/core';

export interface Task {
    name: string;
    completed: boolean;
    color: ThemePalette;
    subtasks?: Task[];
}


@Component({
    selector: 'acs-admin',
    templateUrl: './admin.component.html',
    styleUrls: ['./admin.component.scss'],
    encapsulation: ViewEncapsulation.None



})

export class AcsAdminComponent  {   
    
    tabIndex = 0;
    changeTab(event) {
        this.tabIndex = event.index;
    }
}
