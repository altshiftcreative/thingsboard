import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Dialog } from '@material-ui/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AcsService } from '../../acs-service';


@Component({
    selector: 'presets-dialog',
    templateUrl: './presets-dialog.component.html',
    styleUrls: ['./presets-dialog.component.scss'],


})


export class PresetsDialog implements OnInit {
    constructor(private http: HttpClient, @Inject(MAT_DIALOG_DATA) public data: {_id: string, channel: string,weight: string, schedule: string,events: string, precondition: string,provision: string,provisionArgs: string},private acsService: AcsService) { }
    name: null;
    presetsForm: FormGroup;
    public  flag;

    ngOnInit() {


        if (this.data) {
            this.flag=true;
            this.presetsForm = new FormGroup({
                'presetsName': new FormControl(this.data._id, Validators.required),
                'channel': new FormControl(this.data.channel),
                'weight': new FormControl(this.data.weight),
                'schedule': new FormControl(this.data.schedule),
                'event': new FormControl(this.data.events),
                'provision': new FormControl(this.data.provision, Validators.required),
                'precondition': new FormControl(this.data.precondition),
                'arguments': new FormControl(this.data.provisionArgs),
                });
        } else {
           this.flag=false;
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
    this.acsService.progress('Ceated', true);
    }

}
