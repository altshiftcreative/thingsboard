import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { LwService } from '../../Lw-service';


@Component({
    selector: 'form-dialog',
    templateUrl: './form.component.html',
    styleUrls: ['./form.component.scss'],

})


export class formDialog implements OnInit {
    constructor(private lwService: LwService, private http: HttpClient) { }
    newInstanceForm: FormGroup;
    resourcesArray = this.lwService.formData;
    formGroupObject = {};
    dataType = this.lwService.formType;
    ngOnInit() {

        for (let item of this.resourcesArray) {
            if (item['mandatory']) this.formGroupObject[item['name']] = new FormControl(null, Validators.required);
            else this.formGroupObject[item['name']] = new FormControl(null);
        }
        this.newInstanceForm = new FormGroup(this.formGroupObject);
    }


    async onSubmit() {
        if (this.lwService.formType['type'] != 'write') {
            let dataNames = [];
            this.resourcesArray.forEach((item, index) => {
                if (this.newInstanceForm.value[item['name']] != null) {
                    if (isNaN(this.newInstanceForm.value[item['name']]))
                        dataNames.push({ "id": item['id'], "value": this.newInstanceForm.value[item['name']] })
                    else
                        dataNames.push({ "id": item['id'], "value": parseInt(this.newInstanceForm.value[item['name']]) })

                }
            })

            if (this.lwService.formType['type'] == 'update') {

                await this.http.put(this.lwService.lwm2mBaseUri + '/api/v1/Lw/update/?endpoint=' + this.lwService.clientEndpoint + '&value=' + this.lwService.value + '&format=' + this.lwService.format + '&timeout=' + this.lwService.timeout + '&replace=' + 'false',
                    {
                        "id": this.lwService.value[1],
                        "resources": dataNames
                    }
                ).toPromise().then((dta) => { })
                this.lwService.progress("CHANGED", true);
            }

            else if (this.lwService.formType['type'] == 'create') {
                await this.http.post(this.lwService.lwm2mBaseUri + '/api/v1/Lw/instance/?endpoint=' + this.lwService.clientEndpoint + '&value=' + this.lwService.value + '&format=' + this.lwService.format + '&timeout=' + this.lwService.timeout,

                    {
                        "resources": dataNames
                    }
                ).toPromise().then((dta) => { })
                this.lwService.progress("CREATED", true);
            }
        }

        else if (this.lwService.formType['type'] == 'write') {
            let enterdValue;
            
            if (this.newInstanceForm.value[this.lwService.formData[0]['name']] != null) {
                if (isNaN(this.newInstanceForm.value[this.lwService.formData[0]['name']])) {
                     enterdValue = this.newInstanceForm.value[this.lwService.formData[0]['name']]
                }
                else {
                     enterdValue = parseInt(this.newInstanceForm.value[this.lwService.formData[0]['name']]);
                }
            }
            
            await this.http.put(this.lwService.lwm2mBaseUri + '/api/v1/Lw/write/?endpoint=' + this.lwService.clientEndpoint + '&value=' + this.lwService.value + '&format=' + this.lwService.format + '&timeout=' + this.lwService.timeout,

                {
                    "id": this.lwService.formType['id'],
                    "value": enterdValue
                }
            ).toPromise().then((writeData) => {
                if (writeData['failure']) {
                    this.lwService.progress(writeData['status'], false);
                }
                else {
                    this.lwService.finalWriteValue = enterdValue
                    this.lwService.progress("SUCCESS", true);
                }
            })
            
        }
    }
}
