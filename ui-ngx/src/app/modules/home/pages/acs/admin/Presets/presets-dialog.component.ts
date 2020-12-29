import { Component, ElementRef, ViewChild } from '@angular/core';
import { ThemePalette } from '@angular/material/core';
import { FormControl, FormGroup, FormBuilder, Validators, FormsModule, NgForm } from '@angular/forms'
import { AcsAdminPresetsComponent } from './admin-presets.component';
import { NgModule } from '@angular/core';
import {ACSModule} from '../../acs.module'


@Component({
    selector: 'presets-dialog',
    templateUrl: './presets-dialog.component.html',
    // styleUrls: ['./presets-dialog.component.scss'],


})

 
export class PresetsDialog {

@ViewChild('f') presetsForm:NgForm;

    presetsData2={

        name:'',
        channel:'',
        weight:'',
        schedule:'',
        event:'',
        provision:'',
        precondition:'',
        arguments:'',

    }
onSubmit(){
    console.log("Hi")
    console.log("Hi again u ass hole"+this.presetsForm);
    this.presetsData2.name = this.presetsForm.value.presetsData.name;
    console.log("hiiiii again u ass hole"+this.presetsForm);

    // this.user.email = this.signupForm.value.userData.email;
    // this.user.secretQuestion = this.signupForm.value.secret;
    // this.user.answer = this.signupForm.value.questionAnswer;
    // this.user.gender = this.signupForm.value.gender;

}

suggestUserName() {
    const suggestedName = 'Superuser';
  }

}
