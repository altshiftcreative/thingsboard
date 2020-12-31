import { Component, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, ElementRef } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ThemePalette } from '@angular/material/core';
import _, { kebabCase } from 'lodash';
import { AcsService } from '../../acs-service';
import { DialogDataDialog } from '../../devices/Dialog.component';
import { PresetsDialog } from './presets-dialog.component';
export interface Task {
    name: string;
    completed: boolean;
    color: ThemePalette;
    subtasks?: Task[];
}



@Component({
    selector: 'admin-presets',
    templateUrl: './admin-presets.component.html'
})




export class AcsAdminPresetsComponent implements OnInit, AfterViewInit {

    isTag: Boolean = false;
    tagValue: string;
    isLoading: Boolean = false;
    isOnline: Boolean = false;
    isPast: Boolean = false;
    isOther: Boolean = false;
    csvDataArray: string[][] = [['Name', 'Channel', 'Weight', 'Schedule', 'Events', 'Precondition', 'Provision', 'Arguments']]
    @ViewChild(MatPaginator) paginator: MatPaginator;
    checkedItems: string[] = [];
    constructor(private http: HttpClient, public dialog: MatDialog, private acsService: AcsService) { }
    displayedColumns: string[] = ['Name', 'Channel', 'Weight', 'Schedule', 'Events', 'Precondition', 'Provision', 'Arguments', 'Action'];
    dataSource: MatTableDataSource<any>;
    private httpOptions = {
        headers: new HttpHeaders({
            'Content-Type': 'application/json',
            'Accept': 'application/json'
        }),
        withCredentials: true
    };


    ngOnInit(): void {
    }

    ngAfterViewInit() {

        this.getAdminPresets()
    }

    getAdminPresets() {

        this.http.get<any[]>('http://localhost:8080/api/v1/tr69/presets').subscribe((presetsData) => {
            this.dataSource = new MatTableDataSource(presetsData)
            this.dataSource.paginator = this.paginator;
        })

    }


    deletePresets() {
        if (this.checkedItems.length == 0) { alert("choose a device"); }
        else {
            let confirmation = confirm('Deleting ' + this.checkedItems.length + ' presets. Are you sure?');
            if (confirmation == true) {
                this.checkedItems.forEach((id) => {
                    let ide = encodeURIComponent(id);
                    this.http.delete('http://localhost:8080/api/v1/tr69/presets/?presetsId=' + ide).subscribe((dta) => {
                    })

                });
            }
        }
    }
    toggleVisibility(event) {
        if (event.target.checked) {
            this.checkedItems.push(event.target.name);
            console.log("Array Checked", this.checkedItems);

        }
        else {
            this.acsService.removeItem(this.checkedItems, event.target.name);
        }
    }

    downloadDevicePresetsDataCSV() {
        this.dataSource['filteredData'].forEach((item) => {
            let newArray = [];
            newArray.push(item['_id']);
            newArray.push(item['weight']);
            newArray.push(item['channel']);
            newArray.push(item['events']);
            newArray.push(item['precondition']);
            newArray.push(item['schedule']);
            newArray.push(item['provision']);
            newArray.push(item['provisionArgs']);
            this.csvDataArray.push(newArray);
        })

        let d = new Date().toISOString();
        let csvContent = "data:text/csv;charset=utf-8," + this.csvDataArray.map(e => e.join(",")).join("\n");
        let encodedUri = encodeURI(csvContent);
        let link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "Presets-" + d + ".csv");
        document.body.appendChild(link);
        link.click();
        this.csvDataArray = [['Name', 'Channel', 'Weight', 'Schedule', 'Events', 'Precondition', 'Provision', 'Arguments']];

    }

    presetsSearch(event) {

        this.http.get('http://localhost:8080/api/v1/tr69/searchpresets/?_id=' + event.target.value).subscribe((result: any[]) => {
            this.dataSource.data = result;

        })
    }

    openDialog(row) {
        if (row) {
            this.dialog.open(PresetsDialog,
                {
                    data: row,
                    height: '400px',
                    width: '600px',
                  
                },
                
            );
        } else {
            this.dialog.open(PresetsDialog,{
                height: '400px',
                width: '600px',

            });
        }
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
                this.checkedItems = [];
            }
        }
    }

}



