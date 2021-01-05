import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";

@Component({
    selector: 'Lw-clients',
    templateUrl: './clients.component.html',
    styleUrls: ['./clients.component.scss']
})


export class LwClientsComponent implements OnInit, AfterViewInit {
    dataSource: MatTableDataSource<any>;
    displayedColumns: string[] = ['Client Endpoint', 'Registration ID', 'Registration Date', 'Last Update', 'Action'];
    @ViewChild(MatPaginator) paginator: MatPaginator;



    clientsCounter: number ;
    constructor(private http: HttpClient) { }

    ngAfterViewInit(): void {
        this.getClients();
        
    }
    ngOnInit(): void {
        
    }

    getClients(){
        
        this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clients', { withCredentials: true }).subscribe((clientsData) => {
            this.dataSource = new MatTableDataSource(clientsData)
            this.dataSource.paginator = this.paginator;
            this.clientsCounter = clientsData.length;
            
        })
    }

    clientsSearch(event){
        if (event.target.value == "") {}
        // this.myDataSouce.data = this.acsService.deviceArrayData;
        else {
            // let arrayContainer = [];
            // this.acsService.deviceArrayData.forEach((element) => {
            //     if (element.parameter.toLowerCase().includes(event.target.value.toLowerCase())) {
            //         arrayContainer.push(element);
            //     }
            // })
            // this.myDataSouce.data = arrayContainer;
        }
    }
}