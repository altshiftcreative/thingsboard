import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Dialog } from '@material-ui/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AcsService } from '../../acs-service';


@Component({
    selector: 'config-dialog',
    templateUrl: './config-dialog.component.html',
    // styleUrls: ['./presets-dialog.component.scss'],


})


export class configDialog implements OnInit {
    
    constructor(private http: HttpClient,@Inject(MAT_DIALOG_DATA) public data: {_id: string, value: string},private acsService: AcsService) { }
    name: null;
    configForm: FormGroup;


    ngOnInit() {

        if (this.data) {
            this.configForm = new FormGroup({
                'configName': new FormControl(this.data._id, Validators.required),
                'value': new FormControl(this.data.value),
            });
        } else {
            this.configForm = new FormGroup({
                'configName': new FormControl(null, Validators.required),
                'value': new FormControl(null),
            });
        }
        
    }
    onSubmit() {


        this.http.put('http://localhost:8080/api/v1/tr69/config/?configId=' + this.configForm.value.configName,

        {
            "value": this.configForm.value.value,
  
        }
    ).subscribe((dta) => { })
    
    this.acsService.progress('Created', true);
    }
   
}
