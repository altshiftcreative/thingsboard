import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AcsService } from '../../acs-service';


@Component({
    selector: 'config-dialog',
    templateUrl: './config-dialog.component.html',
})


export class configDialog implements OnInit {

    constructor(private http: HttpClient, @Inject(MAT_DIALOG_DATA) public data: { _id: string, value: string }, private acsService: AcsService) { }
    name: null;
    configForm: FormGroup;
    public flag;

    ngOnInit() {

        if (this.data) {
            this.flag = true;
            this.configForm = new FormGroup({
                'configName': new FormControl(this.data._id, Validators.required),
                'value': new FormControl(this.data.value),
            });
        } else {
            this.flag = false;
            this.configForm = new FormGroup({
                'configName': new FormControl(null, Validators.required),
                'value': new FormControl(null),
            });
        }

    }
    onSubmit() {


        this.http.put(this.acsService.acsBaseUri + '/api/v1/tr69/config/?configId=' + this.configForm.value.configName,

            {
                "value": this.configForm.value.value,

            }
        ).subscribe((dta) => { })

        this.acsService.progress('Created', true);
    }

}
