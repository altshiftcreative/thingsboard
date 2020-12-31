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
    
    changeButton=1;
    changeButton1(){
        this.changeButton = 1;

    }
    changeButton2(){
        this.changeButton = 2;

    }
    changeButton3(){
        this.changeButton = 3;

    }
}
