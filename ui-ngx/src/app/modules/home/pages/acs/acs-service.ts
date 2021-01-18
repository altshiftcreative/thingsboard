import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DialogAlert } from './popup/popup-show';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Store } from '@ngrx/store';
import { AppState } from '@app/core/core.state';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { NotificationType } from '@app/core/notification/notification.models';

@Injectable({
    providedIn: 'root'
})

export class AcsService {
    public online_devices = 0;
    public past_devices = 0;
    public others_devices = 0;
    public online_counter = 1;
    public deviceArrayData = [];
    public acsBaseUri=""

    constructor(private http: HttpClient, public dialog: MatDialog, private _snackBar: MatSnackBar, protected store: Store<AppState>) { }


    public removeItem(array, item) {
        for (var i in array) {
            if (array[i] == item) {
                array.splice(i, 1);
                break;
            }
        }
    }

    public async change(id, parameterName, newValue, element): Promise<any> {
        if (element['onlineStatus'] == 'Online' || element == 'Online') {
            await this.http.post(this.acsBaseUri+'/api/v1/tr69/tasks/?deviceID=' + id,
                [
                    {
                        "device": id,
                        "name": "setParameterValues",
                        "parameterValues": [
                            [
                                parameterName,
                                newValue,
                                "xsd:string"
                            ]
                        ],
                        "status": "pending"
                    }
                ],
            ).toPromise().then((dta) => { })
            this.progress('Committed', true);
        }
        else {
            this.progress('device is offline', false);
        }
    }

    public async refresh(id, parameterName): Promise<any> {
        await this.http.post(this.acsBaseUri+'/api/v1/tr69/tasks/?deviceID=' + id,
            [
                {
                    "name": "getParameterValues",
                    "device": id,
                    "parameterNames": [
                        parameterName
                    ],
                    "status": "pending"
                }
            ],
        ).toPromise().then((dta) => { })
        this.progress('Refresh', true);
    }


    public async deleteDevice(id): Promise<any> {
        await this.http.delete(this.acsBaseUri+'/api/v1/tr69/devices/?deviceID=' + id).toPromise().then((dta) => { })

    }


    public async deleteFault(id): Promise<any> {
        await this.http.delete(this.acsBaseUri+'/api/v1/tr69/faults/?faultsId=' + id).toPromise().then((dta) => { })
    }


    public async deletePresets(id): Promise<any> {
        await this.http.delete(this.acsBaseUri+'/api/v1/tr69/presets/?presetsId=' + id).toPromise().then((dta) => { })
    }


    public async deleteProvisions(id): Promise<any> {
        await this.http.delete(this.acsBaseUri+'/api/v1/tr69/provisions/?provisionsId=' + id).toPromise().then((dta) => { })
    }

    public async deleteConfig(id): Promise<any> {
        await this.http.delete(this.acsBaseUri+'/api/v1/tr69/config/?configId=' + id).toPromise().then((dta) => { })
    }



    public rebootDevice(id): void {
        this.http.post(this.acsBaseUri+'/api/v1/tr69/tasks/?deviceID=' + id,
            [
                {
                    "name": "reboot",
                    "device": id,
                    "status": "pending"
                }
            ]).subscribe((dta) => { })
    }

    public resetDevice(id): void {
        this.http.post(this.acsBaseUri+'/api/v1/tr69/tasks/?deviceID=' + id,
            [
                {
                    "name": "factoryReset",
                    "device": id,
                    "status": "pending"
                }
            ]).subscribe((dta) => { })
    }

    public async tagDevice(id, tagValue: Record<string, boolean>): Promise<any> {
        await this.http.post(this.acsBaseUri+'/api/v1/tr69/tag/?deviceID=' + id,
            tagValue).toPromise().then((dta) => { })
    }

    public async untagDevice(id, untagValue: Record<string, boolean>): Promise<any> {
        console.log('untaaaaag');

        await this.http.post(this.acsBaseUri+'/api/v1/tr69/tag/?deviceID=' + id,
            untagValue).toPromise().then((dta) => { })
    }

    public onlineStatus(dataSource) {
        dataSource['filteredData'].forEach((item) => {
            if (item['Events.Inform']['value'][0] > Date.now() - 5 * 60 * 1000) {
                item['onlineStatus'] = 'Online';
                if (this.online_counter == 1)
                    this.online_devices++;
            }
            else if (item['Events.Inform']['value'][0] > (Date.now() - 5 * 60 * 1000) - (24 * 60 * 60 * 1000) && item['Events.Inform']['value'][0] < (Date.now() - 5 * 60 * 1000)) {
                item['onlineStatus'] = 'Past 24 hours';
                if (this.online_counter == 1)
                    this.past_devices++;
            }
            else if (item['Events.Inform']['value'][0] < (Date.now() - 5 * 60 * 1000) - (24 * 60 * 60 * 1000)) {
                item['onlineStatus'] = 'Others';
                if (this.online_counter == 1)
                    this.others_devices++;
            }
        })
        this.online_counter++;
    }

    public addInstance(id, name): void {
        this.http.post(this.acsBaseUri+'/api/v1/tr69/tasks/?deviceID=' + id,
            [
                {
                    "name": "addObject",
                    "device": id,
                    "objectName": name,
                    "status": "pending"
                }
            ],
        ).subscribe((dta) => { })
    }

    public statusCounter(deviceData) {
        deviceData.forEach((element) => {
            if (element['Events.Inform']['value'][0] > Date.now() - 5 * 60 * 1000) {
                if (this.online_counter == 1)
                    this.online_devices++;
            }
            else if (element['Events.Inform']['value'][0] > (Date.now() - 5 * 60 * 1000) - (24 * 60 * 60 * 1000) && element['Events.Inform']['value'][0] < (Date.now() - 5 * 60 * 1000)) {
                if (this.online_counter == 1)
                    this.past_devices++;
            }
            else if (element['Events.Inform']['value'][0] < (Date.now() - 5 * 60 * 1000) - (24 * 60 * 60 * 1000)) {
                if (this.online_counter == 1)
                    this.others_devices++;
            }
        })
        this.online_counter = 2;
    }

    progress(res, stat) {
        let type: NotificationType  = stat ? 'success' : 'error'
            this.store.dispatch(new ActionNotificationShow(
                {
                    message: res,
                    type: type,
                    duration: 2000,
                    verticalPosition: 'bottom',
                    horizontalPosition: 'center'
                }));
        
    }

}