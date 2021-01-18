import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { LwService } from "../Lw-service";
import { newInstanceDialog } from "./clients-data-model/createInstance /newInstance-dialog.component";
@Component({
    selector: 'Lw-clients',
    templateUrl: './clients.component.html',
    styleUrls: ['./clients.component.scss']
})


export class LwClientsComponent implements OnInit, AfterViewInit {
    dataSource: MatTableDataSource<any>;
    displayedColumns: string[] = ['Client Endpoint', 'Registration ID', 'Registration Date', 'Last Update', 'Action'];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    clientsArray: any[];
    dataModelComponent: Boolean = false;
    clientsCounter: number;
    clientEndpoint: string;
    myData: any;
    constructor(private http: HttpClient, private lwService: LwService) { }

    ngAfterViewInit(): void {
        this.getClients();

    }
    ngOnInit(): void {
        // const eventSource = new window['EventSource']("http://localhost:8080/api/v1/Lw/clients", {withCredentials: true})
        // eventSource.addEventListener('REGISTRATION', message => {
            
        //     this.myData = message.target;
        //     console.log('eeeeeeeeeeeeeeee :',this.myData);
            
        // });
        // setInterval(this.getClients.bind(this),2000);
       
    }

    getClients() {
        this.http.get<any[]>(this.lwService.lwm2mBaseUri+'/api/v1/Lw/clients', { withCredentials: true }).toPromise().then((clientsData) => {
            this.dataSource = new MatTableDataSource(clientsData)
            this.dataSource.paginator = this.paginator;
            this.clientsCounter = clientsData.length;
            this.clientsArray = clientsData;
        })
    }

    clientsSearch(event) {
        if (event.target.value == "") { this.dataSource.data = this.clientsArray; }

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

    openDataModel(endpoint) {
        this.clientEndpoint = endpoint;
        this.lwService.clientEndpoint = endpoint;
        this.dataModelComponent = true;
    }


}