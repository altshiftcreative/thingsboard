import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Dialog } from '@material-ui/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';


@Component({
    selector: 'provisions-dialog',
    templateUrl: './provisions-dialog.component.html',
    // styleUrls: ['./presets-dialog.component.scss'],


})


export class provisionsDialog implements OnInit {
    
    constructor(private http: HttpClient,@Inject(MAT_DIALOG_DATA) public data: {_id: string, script: string}) { }
    name: null;
    provisionsForm: FormGroup;
    public flag;


    ngOnInit() {

        if (this.data) {
            this.flag=true;
            this.provisionsForm = new FormGroup({
                'provisionsName': new FormControl(this.data._id, Validators.required),
                'script': new FormControl(this.data.script),
            });
        } else {
            this.flag=false;
            this.provisionsForm = new FormGroup({
                'provisionsName': new FormControl(null, Validators.required),
                'script': new FormControl(null),
            });
        }
        
    }
    onSubmit() {


        this.http.put('http://localhost:8080/api/v1/tr69/provisions/?provisionsId=' + this.provisionsForm.value.provisionsName,

        {
            "script": this.provisionsForm.value.script,
  
        }
    ).subscribe((dta) => { })
    }
   
}
