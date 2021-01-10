import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LwClientSecurityConfigComponent } from './clientSecurityConfig.component';


@Component({
    selector: 'newConfig-dialog',
    templateUrl: './newConfig-dialog.component.html',
    styleUrls: ['./newConfig-dialog.component.scss'],

})


export class newConfigDialog implements OnInit {
    constructor(private http: HttpClient, @Inject(MAT_DIALOG_DATA) public data: {endpoint: string, identity: string,key: string}) { }
    newConfigForm: FormGroup;
    ngOnInit() {
        this.newConfigForm = new FormGroup({

            'endpoint': new FormControl(null, Validators.required),
            'identity': new FormControl(null),
            'key': new FormControl(null),
        });
    }

    
    onSubmit() {
        console.log(this.newConfigForm);
        this.http.put('http://localhost:8080/api/v1/Lw/addClientsSecurity',

        { 
            "endpoint": this.newConfigForm.value.endpoint,
            "psk":{
                "identity": this.newConfigForm.value.identity,
                "key": this.newConfigForm.value.key,
            }
        }
    ).subscribe((dta) => { })
    }
}
