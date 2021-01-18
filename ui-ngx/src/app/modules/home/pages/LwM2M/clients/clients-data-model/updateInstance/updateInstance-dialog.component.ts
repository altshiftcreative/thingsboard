import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms'
import { HttpClient } from '@angular/common/http';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { LwService } from '../../../Lw-service';


@Component({
    selector: 'updateInstance-dialog',
    templateUrl: './updateInstance-dialog.component.html',
    styleUrls: ['./updateInstance-dialog.component.scss'],

})


export class updateInstanceDialog implements OnInit {
    constructor(private lwService: LwService, private http: HttpClient) { }
    newInstanceForm: FormGroup;
    resourcesArray: object[] = [];
    ngOnInit() {
        this.newInstanceForm = new FormGroup({

            'Lifetime': new FormControl(null, Validators.required),
            'DefaultMinimum': new FormControl(null),
            'DefaultMaximum': new FormControl(null),
            'DisableTimeout': new FormControl(null),
            'Binding': new FormControl(null, Validators.required),
            'Notification': new FormControl(null, Validators.required),

        });
    }


    async onSubmit(action) {
        let dataNames = [{ "id": 1, "value": this.newInstanceForm.value.Lifetime }, { "id": 2, "value": this.newInstanceForm.value.DefaultMinimum }, { "id": 3, "value": this.newInstanceForm.value.DefaultMaximum }, { "id": 5, "value": this.newInstanceForm.value.DisableTimeout }, { "id": 7, "value": this.newInstanceForm.value.Binding }, { "id": 6, "value": this.newInstanceForm.value.Notification }]
        let replaceStatus='false';
        if(action == 'Replace') replaceStatus = 'true';
        dataNames.forEach(async e => {
            if (e['value'] != null && e['value'] != NaN) {
                await this.resourcesArray.push(e);
            }
        })

        await this.http.put(this.lwService.lwm2mBaseUri+'/api/v1/Lw/update/?endpoint=' + this.lwService.clientEndpoint + '&value=' + this.lwService.value + '&format=' + this.lwService.format + '&timeout=' + this.lwService.timeout + '&replace=' + replaceStatus,
            
            {
                "id": this.lwService.value[1],
                "resources": this.resourcesArray
            }
        ).toPromise().then((dta) => { })
        this.lwService.progress("CHANGED", true);

    }
}
