import { Component, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ThemePalette } from '@angular/material/core';
import _, { kebabCase } from 'lodash';
import { AcsService } from '../acs-service';
export interface Task {
    name: string;
    completed: boolean;
    color: ThemePalette;
    subtasks?: Task[];
}


@Component({
    selector: 'acs-faults',
    templateUrl: './faults.component.html'
})




export class AcsFaultsComponent implements OnInit, AfterViewInit {
    isTag: Boolean = false;
    tagValue: string;
    // test: string = 'Tags.qq';
    isLoading: Boolean = false;
    @ViewChild(MatPaginator) paginator: MatPaginator;
    checkedItems: string[] = [];
    constructor(private http: HttpClient, public dialog: MatDialog, private acsService: AcsService) { }
    displayedColumns: string[] = ['Device Name','SSID', 'Last Inform', 'IP', 'Tag', 'Action','Timestamp'];
    dataSource: MatTableDataSource<any>;
    ngOnInit(): void {
        // this.http.post('http://127.0.0.1:3000/login', {
        //     "username": "admin",
        //     "password": "admin"
        // }, { withCredentials: true }).subscribe(data => {
        //     this.http.get<any[]>('http://localhost:3000/api/devices', { withCredentials: true }).subscribe((deviceData) => {
        //         this.dataSource = new MatTableDataSource(deviceData)
        //         this.dataSource.paginator = this.paginator;
        // this.http.post('http://127.0.0.1:3000/login', {
        //     "username": "admin",
        //     "password": "admin"
        // }, { withCredentials: true }).subscribe(data => {
        //     this.http.get<any[]>('http://localhost:3000/api/devices', { withCredentials: true }).subscribe((deviceData) => {
        //         this.dataSource = new MatTableDataSource(deviceData)
        //         this.dataSource.paginator = this.paginator;

        //     })
        // })


       
    }

    ngAfterViewInit() {
        this.http.get<any[]>('http://localhost:3000/api/faults/?filter=true').subscribe((deviceData) => {
            console.log(deviceData);
        })
    }

    getRecord(row) {
        console.log(row)
    }


    updateValue(deviceID, SSIDvalue, parameterName) {
        let newValue = prompt(parameterName, SSIDvalue);
        this.acsService.change(deviceID, parameterName, newValue);
        console.log('comeonnn', parameterName);


    };


    toggleVisibility(event) {
        // console.log("eventtt", event.target.name);
        if (event.target.checked) {
            this.checkedItems.push(event.target.name);
            console.log("Array Checked", this.checkedItems);

        }
        else {
            // this.acsService.removeA(this.checkedItems,event.target.name)
            this.acsService.removeItem(this.checkedItems, event.target.name);
        }
    }

    operations(type,e) {
        if (this.checkedItems.length == 0) { alert("choose a device"); }
        else {
            switch (type) {
                case "delete":
                    this.checkedItems.forEach((i) => {
                        this.acsService.deleteDevice(i);
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
                    // let tagsArray = Object.keys(e).filter(k => k.startsWith('Tags.'));
                    this.checkedItems.forEach((i) => {
                        this.acsService.tagDevice(i, { [tagValue]: true });
                    });
                    break;
                case "untag":
                    let untagValue = prompt("Enter tag to unassign from " + this.checkedItems.length + " devices:");
                    if (untagValue == this.tagValue) {
                        this.tagValue = "";
                        this.isTag = false;
                        this.checkedItems.forEach((i) => {
                            this.acsService.untagDevice(i, { [untagValue]: false });
                        });
                    }
                    break;
            }
        }
    }

    
}
