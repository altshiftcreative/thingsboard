import { Component, ViewChild, AfterViewInit, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ThemePalette } from '@angular/material/core';
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

    @ViewChild(MatPaginator) paginator: MatPaginator;
    checkedItems: string[] = [];
    constructor(private http: HttpClient, public dialog: MatDialog, private acsService: AcsService) { }

    displayedColumns: string[] = ['Device', 'Channel', 'Code', 'Message', 'Detail', 'Retries', 'Timestamp'];
    csvDataArray: string[][] = [['Device', 'Channel', 'Code', 'Message', 'Detail', 'Retries', 'Timestamp']]
    dataSource: MatTableDataSource<any>;
    ngOnInit(): void {
       this.getFaults();
    }

    ngAfterViewInit() {

    }

    getFaults(){
        this.http.get<any[]>('http://localhost:8080/api/v1/tr69/faults', { withCredentials: true }).subscribe((deviceData) => {
            this.dataSource = new MatTableDataSource(deviceData)
            this.dataSource.paginator = this.paginator;
            this.checkedItems=[]
        })
    }
    // ========================Faults Methods============================ //

    toggleVisibility(event) {
        console.log("eventtt", event.target.name);
        if (event.target.checked) {
            this.checkedItems.push(event.target.name);
            console.log("Array Checked", this.checkedItems);

        }
        else {
            // this.acsService.removeA(this.checkedItems,event.target.name)
            // delete this.checkedItems[event.target.name];
            this.acsService.removeItem(this.checkedItems, event.target.name);
            console.log("Array UnChecked", this.checkedItems);

        }
    }

     async deleteFaults() {
        if (this.checkedItems.length == 0) {this.acsService.progress('Choose a fault', false); }
        else {
            let confirmation = confirm('Deleting ' + this.checkedItems.length + ' faults. Are you sure?');
            if (confirmation == true) {
                for(let e of this.checkedItems){
                    await this.acsService.deleteFault(e);
                }
                this.acsService.progress('Deleted', true);
                this.getFaults();
            }
        }
    }
    downloadDeviceFaultsDataCSV() {
        this.dataSource['filteredData'].forEach((item) => {
            let newArray = [];
            newArray.push(item['_id']);
            newArray.push(item['channel']);
            newArray.push(item['code']);
            newArray.push(item['message']);
            newArray.push(item['detail']['faultCode']);
            newArray.push(item['retries']);
            newArray.push(this.timestampConvertor(item['timestamp']));
            this.csvDataArray.push(newArray);
        })

        let d = new Date().toISOString();
        let csvContent = "data:text/csv;charset=utf-8," + this.csvDataArray.map(e => e.join(",")).join("\n");
        let encodedUri = encodeURI(csvContent);
        let link = document.createElement("a");
        link.setAttribute("href", encodedUri);
        link.setAttribute("download", "faults-" + d + ".csv");
        document.body.appendChild(link);
        link.click();
        this.csvDataArray = [['Device', 'Channel', 'Code', 'Message', 'Detail', 'Retries', 'Timestamp']];

    }

    timestampConvertor(timestamp) {
        let date = new Date(timestamp);
        return date.toDateString() + ' ' + date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
    }

    faultsSearch(event) {
        // event.toLowerCase;
        let str1: string = event.target.value;
        // let deviceId = str1.split(':', 1);

        // 202BC1-BM632w-000004:task_5fe5aac3ae942d20943c50fa => target.value
        this.http.get('http://localhost:8080/api/v1/tr69/searchfaults/?device=' + str1.toLowerCase()).subscribe((result: any[]) => {
            this.dataSource.data = result;

        })

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

    // ========================Faults Methods============================ //

}
