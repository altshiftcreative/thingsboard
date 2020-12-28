import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AcsService } from '../acs-service';


@Component({
    selector: 'acs-device',
    templateUrl: './device.component.html',
    styleUrls: ['./device.component.scss']
})


export class AcsDeciveComponent implements OnInit, AfterViewInit {
    devices: Boolean = true;
    isTag: Boolean = false;
    tagValue: string;
    isLoading: Boolean = false;
    isOnline: Boolean = false;
    isPast: Boolean = false;
    isOther: Boolean = false;
    csvParameter;
    csvDataArray: string[][] = [['Device Name', 'SSID', 'Last Inform', 'IP', 'Tag']]
    csvDataArrayParameter: string[][] = [['Parameter', 'Object', 'Object timestamp', 'Writable', 'Writable timestamp', 'Value', 'Value type', 'Value timestamp', 'Notification', 'Notification timestamp', 'Access list', 'Access list timestamp']]
    @ViewChild(MatPaginator) paginator: MatPaginator;
    checkedItems: string[] = [];
    constructor(private http: HttpClient, public dialog: MatDialog, private acsService: AcsService) { }
    displayedColumns: string[] = ['Device Name', 'SSID', 'Last Inform', 'IP', 'Online', 'Tag', 'Action'];
    dataSource: MatTableDataSource<any>;
    ngOnInit(): void {
    }
    ngAfterViewInit() {
        this.http.get<any[]>('http://localhost:8080/api/v1/tr69/devices', { withCredentials: true }).subscribe((deviceData) => {
            this.dataSource = new MatTableDataSource(deviceData)
            this.dataSource.paginator = this.paginator;
            this.onlineStatus();
            this.tagsCheck();
        })
    }
    onlineStatus() {
        this.acsService.onlineStatus(this.dataSource);
    }

    tagsCheck() {
        this.dataSource.filteredData.forEach((item) => {
            let tagsArray = Object.keys(item).filter(k => k.startsWith('Tags.'));
            if (tagsArray.length > 0) {
                let tagsPureNameArray = this.stringSplit(tagsArray);
                item['Tags_Pure'] = tagsPureNameArray;
            }
        })
    }

    openDialog(row) {
        this.csvParameter = row;
        this.checkedItems = [];
        this.isLoading = true;
        this.checkedItems.push(row['DeviceID.ID']['value'][0]);
        let DeviceObject = Object.values(row)
        let DeviceKeys = Object.keys(row)
        const deviceArray = DeviceKeys.map((item, index) => ({ parameter: DeviceKeys[index], deviceData: DeviceObject[index] }))
        console.log('deviceArray', deviceArray);
        this.acsService.deviceArrayData = deviceArray;
        this.devices = false;
        this.isLoading = false;
    }

    updateValue(deviceID, SSIDvalue, parameterName) {
        let newValue = prompt(parameterName, SSIDvalue);
        this.acsService.change(deviceID, parameterName, newValue);
    };

    stringSplit(arr) {
        let pureTagsArray = [];
        arr.forEach(element => {
            let res = element.split(".");
            pureTagsArray.push(res[1])
        });
        return pureTagsArray;
    }


    toggleVisibility(event) {
        if (event.target.checked) {
            this.checkedItems.push(event.target.name);
        }
        else {
            this.acsService.removeItem(this.checkedItems, event.target.name);
        }
    }

    operations(type, e) {
        if (this.checkedItems.length == 0) { alert("choose a device"); }
        else {
            switch (type) {
                case "delete":
                    this.checkedItems.forEach((i) => {
                        this.acsService.deleteDevice(i);
                        this.devices = true;
                    });
                    break;
                case "reboot":
                    this.checkedItems.forEach((i) => {
                        this.acsService.rebootDevice(i);
                    });
                    break;
                case "reset":
                    this.checkedItems.forEach((i) => {
                        this.acsService.resetDevice(i);
                    });
                    break;
                case "tag":
                    let tagValue = prompt("Enter tag to assign to " + this.checkedItems.length + " devices:");
                    this.tagValue = tagValue;
                    this.isTag = true;
                    this.checkedItems.forEach((i) => {
                        this.acsService.tagDevice(i, { [tagValue]: true });
                    });
                    break;
                case "untag":
                    let untagValue = prompt("Enter tag to unassign from " + this.checkedItems.length + " devices:");
                    console.log('kkk', untagValue);


                    this.tagValue = "";
                    this.isTag = false;
                    this.checkedItems.forEach((i) => {
                        this.acsService.untagDevice(i, { [untagValue]: false });
                    });

                    break;
            }
        }
    }

    dateConvertor(timestamp) {
        let date = new Date(timestamp);
        return date.toDateString() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
    }

    downloadCSV() {
        if (this.devices) {
            this.dataSource['filteredData'].forEach((item) => {
                let newArray = [];
                newArray.push(item['DeviceID.ID']['value'][0]);
                newArray.push(item['InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.SSID']['value'][0]);
                newArray.push(this.dateConvertor(item['Events.Inform']['value'][0]));
                newArray.push(item['InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1.ExternalIPAddress']['value'][0]);
                this.csvDataArray.push(newArray);
            })
            this.CSV(this.csvDataArray);
            this.csvDataArray = [['Device Name', 'SSID', 'Last Inform', 'IP', 'Tag']];
        }
        else {
            for (const [key, value] of Object.entries(this.csvParameter)) {
                let newArray = [];
                newArray.push(key);
                newArray.push(value['object']);
                newArray.push(value['objectTimestamp']);
                newArray.push(value['writable']);
                newArray.push(value['writableTimestamp']);
                if (value['value']) {
                    newArray.push(value['value'][0]);
                    newArray.push(value['value'][1]);
                    newArray.push(value['valueTimestamp']);
                }
                this.csvDataArrayParameter.push(newArray);
            }
            this.CSV(this.csvDataArrayParameter);
            this.csvDataArrayParameter = [['Parameter', 'Object', 'Object timestamp', 'Writable', 'Writable timestamp', 'Value', 'Value type', 'Value timestamp', 'Notification', 'Notification timestamp', 'Access list', 'Access list timestamp']]
        }
    }

    CSV(csvArray) {
        let d = new Date().toISOString();
        let csvContent = "data:text/csv;charset=utf-8," + csvArray.map(e => e.join(",")).join("\n");
        let encodedUri = encodeURI(csvContent);
        let link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "devices-" + d + ".csv");
        document.body.appendChild(link);
        link.click();
    }

    liveSearch(event) {
        this.http.get('http://localhost:8080/api/v1/tr69/search/?serialNumber=' + event.target.value).subscribe((result: any[]) => {
            this.dataSource.data = result;
            this.onlineStatus();
            this.tagsCheck();
        })
    }

    checkAll(event) {
        let x = document.getElementsByClassName('checkboxes');
        if (event.target.checked) {
            for (let i = 0; i < x.length; i++) {
                x[i]['checked'] = true;
                this.checkedItems.push(x[i]['name']);
            }
        }
        else {
            for (let i = 0; i < x.length; i++) {
                x[i]['checked'] = false;
            }
        }
    }
}



