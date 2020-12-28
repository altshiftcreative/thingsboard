import { Component } from '@angular/core';
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


})




export class AcsAdminComponent  {
    
}
