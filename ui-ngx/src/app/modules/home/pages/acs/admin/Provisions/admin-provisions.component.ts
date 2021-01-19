import { Component, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, ElementRef } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ThemePalette } from '@angular/material/core';
import _, { kebabCase } from 'lodash';
import { AcsService } from '../../acs-service';
import { provisionsDialog } from './provisions-dialog.component';
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
    csvParameter;
    constructor(private http: HttpClient, public dialog: MatDialog, private acsService: AcsService) { }
    displayedColumns: string[] = ['_id', 'script', 'Action'];
    dataSource: MatTableDataSource<any>;

    ngOnInit(): void {
        this.getAdminProvisions()
    }

    ngAfterViewInit() {

        
    }

    getAdminProvisions() {

        this.http.get<any[]>(this.acsService.acsBaseUri+'/api/v1/tr69/provisions').subscribe((ProvisionsData) => {
            this.dataSource = new MatTableDataSource(ProvisionsData)
            this.dataSource.paginator = this.paginator;
        })

    }


    async deleteProvisionsData() {
        if (this.checkedItems.length == 0) { alert("choose a device"); }
        else {
            let confirmation = confirm('Deleting ' + this.checkedItems.length + ' provisions. Are you sure?');
            if (confirmation == true) {
                for(let e of this.checkedItems){
                    await this.acsService.deleteProvisions(e);
                }
                this.acsService.progress('Deleted', true);
                this.getAdminProvisions();
            }
        }
    }

    getRecord(row) {
        console.log(row)
    }



    // updateValue(deviceID, SSIDvalue, parameterName) {
    //     let newValue = prompt(parameterName, SSIDvalue);
    //     this.acsService.change(deviceID, parameterName, newValue);
    //     console.log('comeonnn', parameterName);


    // };


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

        this.http.get(this.acsService.acsBaseUri+'/api/v1/tr69/searchprovisions/?_id=' + event.target.value).subscribe((result: any[]) => {
            this.dataSource.data = result;

        })
    }

    openDialog(row) {

        if (row) {
            this.dialog.open(provisionsDialog,
                {
                    data: row,
                    height: '400px',
                    width: '600px',
                  
                },
                
            ).afterClosed().subscribe(result =>{
                this.getAdminProvisions();
            })
        } else {
            this.dialog.open(provisionsDialog,{
                height: '400px',
                width: '600px',

            }).afterClosed().subscribe(result =>{
                this.getAdminProvisions();
            })
        }
        
    }


    checkAll(event) {
        this.checkedItems = [];
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
