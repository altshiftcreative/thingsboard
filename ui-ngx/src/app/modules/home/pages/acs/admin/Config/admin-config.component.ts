import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AcsService } from '../../acs-service';
import { configDialog } from './config-dialog.component';
import { el } from 'date-fns/locale';


@Component({
    selector: 'admin-config',
    templateUrl: './admin-config.component.html',
    styleUrls: ['./admin-config.component.scss']
})


export class AcsAdminConfigComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    public configArrayData = [];
    constructor(private http: HttpClient, public dialog: MatDialog, private acsService: AcsService) { }
    displayedColumns: string[] = ['_id', 'value', 'Action'];
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
        this.getConfig();
    }

    getConfig(){
        this.http.get<any[]>(this.acsService.acsBaseUri+'/api/v1/tr69/config', { withCredentials: true }).subscribe((configData) => {
            this.dataSource = new MatTableDataSource(configData)
            this.dataSource.paginator = this.paginator;
            this.configArrayData = configData;
        })
    }


    openDialog(row) {
        console.log("this is row", row)
        if (row) {
            this.dialog.open(configDialog,
                {
                    data: row,
                    height: '400px',
                    width: '600px',

                },

            ).afterClosed().subscribe(result =>{
                this.getConfig();
            })
           
        } else {
            this.dialog.open(configDialog, {
                height: '400px',
                width: '600px',

            }).afterClosed().subscribe(result =>{
                this.getConfig();
            })
        }

    }
    async deleteConfig(id) {
        let confirmation = confirm('Deleting ' + id + ' config. Are you sure?');
        if (confirmation == true) {
            await this.acsService.deleteConfig(id);
            this.getConfig();
            this.acsService.progress('Deleted', true);
        }
    }
    liveSearchParameter(event) {
        if (event.target.value == "") this.dataSource.data = this.configArrayData;
        else {
            let arrayContainer = [];
            this.configArrayData.forEach((element) => {
                if (element._id.toLowerCase().includes(event.target.value.toLowerCase())) {
                    arrayContainer.push(element);
                }
            })
            this.dataSource.data = arrayContainer;
        }
    }


}



