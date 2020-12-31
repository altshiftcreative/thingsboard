import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AcsService } from '../../acs-service';
import { configDialog } from './config-dialog.component';


@Component({
    selector: 'admin-config',
    templateUrl: './admin-config.component.html',
    styleUrls: ['./admin-config.component.scss']
})


export class AcsAdminConfigComponent implements OnInit, AfterViewInit {
    @ViewChild(MatPaginator) paginator: MatPaginator;
    public configArrayData = [];
    constructor(private http: HttpClient, public dialog: MatDialog) { }
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

        this.http.get<any[]>('http://localhost:8080/api/v1/tr69/config', { withCredentials: true }).subscribe((configData) => {
            this.dataSource = new MatTableDataSource(configData)
            this.dataSource.paginator = this.paginator;
            this.configArrayData=configData;
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

            );
        } else {
            this.dialog.open(configDialog, {
                height: '400px',
                width: '600px',

            });
        }

    }
    deleteConfig(id) {
        this.http.delete('http://localhost:8080/api/v1/tr69/config/?configId=' + id).subscribe((dta) => {
        })
    }
    liveSearchParameter(event){
        console.log('this is event', this.configArrayData)
        console.log('this is event', this.dataSource)

        if(event.target.value == "") this.dataSource.data = this.configArrayData;
        else{let arrayContainer=[];
            this.configArrayData.forEach((element)=>{
                if(element.parameter.toLowerCase().includes(event.target.value.toLowerCase())){
                    arrayContainer.push(element);
                }
            })
            this.dataSource.data = arrayContainer;
        }
    }


}



