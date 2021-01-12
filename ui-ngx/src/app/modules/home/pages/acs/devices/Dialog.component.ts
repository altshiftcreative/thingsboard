import { HttpClient } from '@angular/common/http';
import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AcsService } from '../acs-service';

@Component({
    selector: 'dialog-data-example-dialog',
    templateUrl: 'dialog-data.html',
})

export class DialogDataDialog implements OnInit, AfterViewInit {

    @ViewChild(MatPaginator) paginator: MatPaginator;

    myDataSouce: MatTableDataSource<any>

    constructor(private acsService: AcsService, private http: HttpClient) { }
    displayedColumns: string[] = ['Parameter', 'Value', 'Action'];
    ngOnInit(): void {
        this.myDataSouce = new MatTableDataSource(this.acsService.deviceArrayData);
        this.myDataSouce.paginator = this.paginator;
    }
    ngAfterViewInit(): void {
        this.myDataSouce.paginator = this.paginator;
    }

    async updateValue(parameterName, value) {
        let onlineS;
        this.acsService.deviceArrayData.forEach((e, i) => {
            if (e.parameter == 'onlineStatus') onlineS = e.deviceData;
        })
        let newValue = prompt(parameterName, value);
        let deviceID = this.acsService.deviceArrayData[0].deviceData['value'][0];
        await this.acsService.change(deviceID, parameterName, newValue, onlineS);
        await this.http.get<any[]>('http://localhost:8080/api/v1/tr69/devices').toPromise().then((deviceData) => {
            for (let i of deviceData) {
                if (i['DeviceID.ID']['value'][0] == deviceID) {
                    let deviceArray = [];
                    for (const key in i) {
                        deviceArray.push({ parameter: key, deviceData: i[key] });
                    }
                    this.myDataSouce.data = deviceArray;
                }
            }
        })

    };

    async refreshValue(parameterName) {
        let deviceID = this.acsService.deviceArrayData[0].deviceData['value'][0];
        await this.acsService.refresh(deviceID, parameterName);
        await this.http.get<any[]>('http://localhost:8080/api/v1/tr69/devices').toPromise().then((deviceData) => {
            for (let i of deviceData) {
                if (i['DeviceID.ID']['value'][0] == deviceID) {
                    let deviceArray = [];
                    for (const key in i) {
                        deviceArray.push({ parameter: key, deviceData: i[key] });
                    }
                    this.myDataSouce.data = deviceArray;
                }
            }
        })
    }

    liveSearchParameter(event) {
        if (event.target.value == "") this.myDataSouce.data = this.acsService.deviceArrayData;
        else {
            let arrayContainer = [];
            this.acsService.deviceArrayData.forEach((element) => {
                if (element.parameter.toLowerCase().includes(event.target.value.toLowerCase())) {
                    arrayContainer.push(element);
                }
            })
            this.myDataSouce.data = arrayContainer;
        }
    }

    addInstance(element) {
        let parameterName = element.parameter;
        let id = this.acsService.deviceArrayData[0].deviceData['value'][0];
        this.acsService.addInstance(id, parameterName)
    }
}