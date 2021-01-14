import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LwService } from '../../../Lw-service';


@Component({
    selector: 'newInstance-dialog',
    templateUrl: './newInstance-dialog.component.html',
    styleUrls: ['./newInstance-dialog.component.scss'],

})


export class newInstanceDialog implements OnInit {
    constructor(private lwService: LwService,private http: HttpClient, @Inject(MAT_DIALOG_DATA) public data: { ID: number, Lifetime: number, DefaultMinimum: number, DefaultMaximum: number, DisableTimeout: number, Binding: string, Notification: string }) { }
    newInstanceForm: FormGroup;
    resourcesArray: object[]=[];
    ngOnInit() {
        this.newInstanceForm = new FormGroup({

            'ID': new FormControl(null),
            'Lifetime': new FormControl(null, Validators.required),
            'DefaultMinimum': new FormControl(null),
            'DefaultMaximum': new FormControl(null),
            'DisableTimeout': new FormControl(null),
            'Binding': new FormControl(null, Validators.required),
            'Notification': new FormControl(null, Validators.required),

        });
    }


    async onSubmit() {
        let dataNames = [{ "id": 1, "value": this.newInstanceForm.value.Lifetime}, { "id": 2, "value": this.newInstanceForm.value.DefaultMinimum}, { "id": 3, "value": this.newInstanceForm.value.DefaultMaximum}, { "id": 5, "value": this.newInstanceForm.value.DisableTimeout }, { "id": 7, "value": this.newInstanceForm.value.Binding }, { "id": 6, "value": this.newInstanceForm.value.Notification }]

         dataNames.forEach(async e => {
            if (e['value'] != null && e['value'] != NaN ) {
                await this.resourcesArray.push(e);
            }
        })        

        await this.http.post('http://localhost:8080/api/v1/Lw/instance/?endpoint='+this.lwService.clientEndpoint+'&value='+this.lwService.value+'&format='+this.lwService.format+'&timeout='+this.lwService.timeout,

            {
                "resources": this.resourcesArray
            }
        ).toPromise().then((dta) => { })
        this.lwService.progress("CREATED", true);
        
    }
}
