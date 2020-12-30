import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Dialog } from '@material-ui/core';


@Component({
    selector: 'presets-dialog',
    templateUrl: './presets-dialog.component.html',
    // styleUrls: ['./presets-dialog.component.scss'],


})


export class PresetsDialog implements OnInit {
    constructor(private http: HttpClient) { }
    name: null;
    presetsForm: FormGroup;
    
    ngOnInit() {

        this.presetsForm = new FormGroup({
            'presetsName': new FormControl(null, Validators.required),
            'channel': new FormControl(null),
            'weight': new FormControl(null),
            'schedule': new FormControl(null),
            'event': new FormControl(null),
            'provision': new FormControl(null, Validators.required),
            'precondition': new FormControl(null),
            'arguments': new FormControl(null),
        });
    }
    onSubmit() {
        this.http.put('http://localhost:8080/api/v1/tr69/presets/?presetsId=' + this.presetsForm.value.presetsName,

        {
            "channel": this.presetsForm.value.channel,
            "weight": this.presetsForm.value.weight,
            "schedule": this.presetsForm.value.schedule,
            "events": this.presetsForm.value.event,
            "precondition": this.presetsForm.value.precondition,
            "provision": this.presetsForm.value.provision,
            "provisionArgs": this.presetsForm.value.arguments,
        }
    ).subscribe((dta) => { })
    }

}
