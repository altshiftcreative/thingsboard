import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { LwService } from "../Lw-service";

@Component({
    selector: 'Lw-clients',
    templateUrl: './clients.component.html',
    styleUrls: ['./clients.component.scss']
})


export class LwClientsComponent implements OnInit, AfterViewInit {
    dataSource: MatTableDataSource<any>;
    displayedColumns: string[] = ['Client Endpoint', 'Registration ID', 'Registration Date', 'Last Update', 'Action'];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    clientsArray : any[];
    dataModelComponent: Boolean = false;
    clientsCounter: number ;
    clientEndpoint: string;
    constructor(private http: HttpClient,private lwService: LwService) { }

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
            this.clientsArray = clientsData;
            console.log(this.dataSource)

        })
    }

    clientsSearch(event){
        if (event.target.value == "") {this.dataSource.data = this.clientsArray;}
        
        else {
            let arrayContainer = [];
            this.clientsArray.forEach((element) => {
                if (element.endpoint.toLowerCase().includes(event.target.value.toLowerCase())) {
                    arrayContainer.push(element);
                }
            })
            this.dataSource.data = arrayContainer;
        }
    }

    openDataModel(endpoint){
        this.clientEndpoint = endpoint;
        this.lwService.clientEndpoint = endpoint;
       this.dataModelComponent = true;
    }
}