import { Component, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ThemePalette } from '@angular/material/core';
import _, { kebabCase } from 'lodash';
import { AcsService } from '../../acs-service';
export interface Task {
    name: string;
    completed: boolean;
    color: ThemePalette;
    subtasks?: Task[];
}


@Component({
    selector: 'admin-provisions',
    templateUrl: './admin-provisions.component.html',


})




export class AcsAdminProvisionsComponent implements OnInit, AfterViewInit {
    isTag: Boolean = false;
    tagValue: string;
    isLoading: Boolean = false;
    isOnline: Boolean = false;
    isPast: Boolean = false;
    isOther: Boolean = false;
    csvDataArray: string[][] = [['Name', 'Script']]
    @ViewChild(MatPaginator) paginator: MatPaginator;
    checkedItems: string[] = [];
    constructor(private http: HttpClient, public dialog: MatDialog, private acsService: AcsService) { }
    displayedColumns: string[] = ['_id', 'script','Action'];
    dataSource: MatTableDataSource<any>;

    ngOnInit(): void {

    }

    ngAfterViewInit() {

        this.getAdminProvisions()
    }

    getAdminProvisions() {

        this.http.get<any[]>('http://localhost:8080/api/v1/tr69/provisions').subscribe((ProvisionsData) => {
            this.dataSource = new MatTableDataSource(ProvisionsData)
            this.dataSource.paginator = this.paginator;
            console.log(ProvisionsData)
        })

    }


    deleteProvisionsData() {
        this.checkedItems.forEach((id) => {
            let ide = encodeURIComponent(id);
            this.http.delete('http://localhost:8080/api/v1/tr69/provisions/?provisionsId=' + ide).subscribe((dta) => {
            })

        });
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

    operations(type, e) {
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

    dateConvertor(timestamp) {
        let date = new Date(timestamp);
        return date.toDateString() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
    }

    downloadDeviceProvisionsDataCSV() {
        this.dataSource['filteredData'].forEach((item) => {
            let newArray = [];
            newArray.push(item['_id']);
            newArray.push(item['script']);
            this.csvDataArray.push(newArray);
        })

        let d = new Date().toISOString();
        let csvContent = "data:text/csv;charset=utf-8," + this.csvDataArray.map(e => e.join(",")).join("\n");
        let encodedUri = encodeURI(csvContent);
        let link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "provisions-" + d + ".csv");
        document.body.appendChild(link);
        link.click();
        this.csvDataArray = [['_id', 'script']];

    }

    provisionsSearch(event) {

        this.http.get('http://localhost:8080/api/v1/tr69/searchprovisions/?_id=' + event.target.value).subscribe((result: any[]) => {
            this.dataSource.data = result;

        })
    }
    
}
