import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { ThemePalette } from '@angular/material/core';
import { AcsService } from '../../acs-service';
import { PresetsDialog } from './presets-dialog.component';
export interface Task {
    name: string;
    completed: boolean;
    color: ThemePalette;
    subtasks?: Task[];
}



@Component({
    selector: 'admin-presets',
    templateUrl: './admin-presets.component.html',
    styleUrls: ['./presets-dialog.component.scss']
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
        this.getAdminPresets();
    }

    ngAfterViewInit() {

        
    }

    getAdminPresets() {

        this.http.get<any[]>(this.acsService.acsBaseUri+'/api/v1/tr69/presets').subscribe((presetsData) => {
            this.dataSource = new MatTableDataSource(presetsData)
            this.dataSource.paginator = this.paginator;
        })

    }


    async deletePresets() {
        if (this.checkedItems.length == 0) { this.acsService.progress('Choose a preset', false); }
        else {
            let confirmation = confirm('Deleting ' + this.checkedItems.length + ' presets. Are you sure?');
            if (confirmation == true) {
                for(let e of this.checkedItems){
                    await this.acsService.deletePresets(e);
                }
                this.acsService.progress('Deleted', true);
                this.getAdminPresets();
            }
        }
    }
    toggleVisibility(event) {
        if (event.target.checked) {
            this.checkedItems.push(event.target.name);
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

        this.http.get(this.acsService.acsBaseUri+'/api/v1/tr69/searchpresets/?_id=' + event.target.value).subscribe((result: any[]) => {
            this.dataSource.data = result;

        })
    }

    openDialog(row) {
        if (row) {
            this.dialog.open(PresetsDialog,
                {
                    data: row,
                    height: '600px',
                    width: '600px',
                  
                },
                
            ).afterClosed().subscribe(result =>{
                this.getAdminPresets();
            })
        } else {
            this.dialog.open(PresetsDialog,{
                height: '600px',
                width: '600px',

            }).afterClosed().subscribe(result =>{
                this.getAdminPresets();
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



