import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';


@Component({
    selector: 'newInstance-dialog',
    templateUrl: './newInstance-dialog.component.html',
    styleUrls: ['./newInstance-dialog.component.scss'],

})


export class newInstanceDialog implements OnInit {
    constructor(private http: HttpClient, @Inject(MAT_DIALOG_DATA) public data: {ID: string, Lifetime: string,DefaultMinimum: string,DefaultMaximum:string,DisableTimeout:string,Binding:string,Notification:string}) { }
    newInstanceForm: FormGroup;
    ngOnInit() {
        this.newInstanceForm = new FormGroup({

            'ID': new FormControl(null),
            'Lifetime': new FormControl(null,Validators.required),
            'DefaultMinimum': new FormControl(null),
            'DefaultMaximum': new FormControl(null),
            'DisableTimeout': new FormControl(null),
            'Binding': new FormControl(null,Validators.required),
            'Notification': new FormControl(null,Validators.required),

        });
    }

    
     onSubmit() {
        this.http.post('http://localhost:8080/api/v1/Lw/instance',

        {
            "resources":
                [
                    {
                        "id": 1,
                        "value": this.newInstanceForm.value.Lifetime
                    },
                    {
                        "id": 6,
                        "value": this.newInstanceForm.value.Notification
                    },
                    {
                        "id": 7,
                        "value": this.newInstanceForm.value.Binding
                    },
                   
                ]
        }
    ).subscribe((dta) => { })
       

    }
}
