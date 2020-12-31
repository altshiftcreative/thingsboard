import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { DialogAlert } from './popup/popup-show';
import { MatDialog } from '@angular/material/dialog';

@Injectable({
    providedIn: 'root'
})

export class AcsService {
    public online_devices = 0;
    public past_devices = 0;
    public others_devices = 0;
    public online_counter = 1;
    public deviceArrayData = [];
    

    constructor(private http: HttpClient,public dialog: MatDialog) { }

    
    public removeItem(array, item) {
        for (var i in array) {
            if (array[i] == item) {
                array.splice(i, 1);
                break;
            }
        }
    }

    public async change(id, parameterName, newValue,element): Promise<any> {        
        if(element['onlineStatus'] == 'Online' || element == 'Online'){        
        await this.http.post('http://localhost:8080/api/v1/tr69/tasks/?deviceID=' + id,
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
        this.progress('Committed',true);
        }
        else{
            this.progress('device offline',false);
        }
    }

    public refresh(id, parameterName): void {
        this.http.post('http://localhost:8080/api/v1/tr69/tasks/?deviceID=' + id,
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
        ).subscribe((dta) => { })
    }


    public async deleteDevice(id): Promise<any> {
        await this.http.delete('http://localhost:8080/api/v1/tr69/devices/?deviceID=' + id).toPromise().then((dta) => { })
        
    }


    public async deleteFault(id): Promise<any> {
        await this.http.delete('http://localhost:8080/api/v1/tr69/faults/?faultsId=' + id).toPromise().then((dta) => {})
    }


    public async deletePresets(id): Promise<any> {
        await this.http.delete('http://localhost:8080/api/v1/tr69/presets/?presetsId=' + id).toPromise().then((dta) => {})
    }


    public async deleteProvisions(id): Promise<any> {
        await this.http.delete('http://localhost:8080/api/v1/tr69/provisions/?provisionsId=' + id).toPromise().then((dta) => {})
    }



    public rebootDevice(id): void {
        this.http.post('http://localhost:8080/api/v1/tr69/tasks/?deviceID=' + id,
            [
                {
                    "name": "reboot",
                    "device": id,
                    "status": "pending"
                }
            ]).subscribe((dta) => { })
    }

    public resetDevice(id): void {
        this.http.post('http://localhost:8080/api/v1/tr69/tasks/?deviceID=' + id,
            [
                {
                    "name": "factoryReset",
                    "device": id,
                    "status": "pending"
                }
            ]).subscribe((dta) => { })
    }

    public async tagDevice(id, tagValue: Record<string, boolean>): Promise<any> {
        await this.http.post('http://localhost:8080/api/v1/tr69/tag/?deviceID=' + id,
            tagValue).toPromise().then((dta) => { })
    }

    public async untagDevice(id, untagValue: Record<string, boolean>): Promise<any> {
        await this.http.post('http://localhost:8080/api/v1/tr69/tag/?deviceID=' + id,
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
        this.http.post('http://localhost:8080/api/v1/tr69/tasks/?deviceID=' + id,
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

    progress(res,stat){
        this.dialog.open(DialogAlert, {
          data: {
            response: res,
            status: stat,
          }
        });
}
}