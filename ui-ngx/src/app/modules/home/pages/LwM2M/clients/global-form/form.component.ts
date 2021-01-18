import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
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
    ngOnInit() {
        for (let item of this.resourcesArray) {
            this.formGroupObject[item['name']] = new FormControl(null);
        }
        this.newInstanceForm = new FormGroup(this.formGroupObject);
    }


    async onSubmit(action) {
        let dataNames = [];
        // let dataNames = [{ "id": 1, "value": this.newInstanceForm.value.Lifetime }, { "id": 2, "value": this.newInstanceForm.value.DefaultMinimum }, { "id": 3, "value": this.newInstanceForm.value.DefaultMaximum }, { "id": 5, "value": this.newInstanceForm.value.DisableTimeout }, { "id": 7, "value": this.newInstanceForm.value.Binding }, { "id": 6, "value": this.newInstanceForm.value.Notification }]
        // let replaceStatus='false';
        // if(action == 'Replace') replaceStatus = 'true';
        // dataNames.forEach(async e => {
        //     if (e['value'] != null && e['value'] != NaN) {
        //         await this.resourcesArray.push(e);
        //     }
        // })
        this.resourcesArray.forEach((item, index) => {
            if (this.newInstanceForm.value[item['name']] != null) {
                if(isNaN(this.newInstanceForm.value[item['name']]))
                dataNames.push({ "id": item['id'], "value": this.newInstanceForm.value[item['name']] })
                else
                dataNames.push({ "id": item['id'], "value": parseInt(this.newInstanceForm.value[item['name']]) })

            }
        })

        await this.http.put(this.lwService.lwm2mBaseUri+'/api/v1/Lw/update/?endpoint=' + this.lwService.clientEndpoint + '&value=' + this.lwService.value + '&format=' + this.lwService.format + '&timeout=' + this.lwService.timeout + '&replace=' + 'false',

            {
                "id": this.lwService.value[1],
                "resources": dataNames
            }
        ).toPromise().then((dta) => { })
        this.lwService.progress("CHANGED", true);





    }
}
