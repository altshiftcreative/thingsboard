import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LwService } from '../../Lw-service';


@Component({
    selector: 'newConfig-dialog',
    templateUrl: './newConfig-dialog.component.html',
    styleUrls: ['./newConfig-dialog.component.scss'],

})


export class newConfigDialog implements OnInit {
    constructor(private http: HttpClient, @Inject(MAT_DIALOG_DATA) public data: {endpoint: string, identity: string,key: string},private lwService: LwService) { }
    newConfigForm: FormGroup;
    ngOnInit() {
        this.newConfigForm = new FormGroup({

            'endpoint': new FormControl(null, Validators.required),
            'identity': new FormControl(null),
            'key': new FormControl(null),
        });
    }

    
     onSubmit() {
        this.http.put(this.lwService.lwm2mBaseUri+'/api/v1/Lw/addClientsSecurity',

        { 
            "endpoint": this.newConfigForm.value.endpoint,
            "psk":{
                "identity": this.newConfigForm.value.identity,
                "key": this.newConfigForm.value.key,
            }
        }
    ).subscribe((dta) => {this.lwService.progress('CREATED', true); })
    
    }
}
