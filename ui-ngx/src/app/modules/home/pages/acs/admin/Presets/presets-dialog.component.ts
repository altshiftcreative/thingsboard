import { Component } from '@angular/core';
import { ThemePalette } from '@angular/material/core';
import { FormControl, FormGroup } from '@angular/forms'



export interface Task {
    name: string;
    completed: boolean;
    color: ThemePalette;
    subtasks?: Task[];
}


@Component({
    selector: 'presets-dialog',
    templateUrl: './presets-dialog.component.html',


})




export class PresetsDialog  {
    
}
 