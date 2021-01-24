import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnDestroy, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { LwService } from "../Lw-service";
@Component({
    selector: 'Lw-clients',
    templateUrl: './clients.component.html',
    styleUrls: ['./clients.component.scss']
})


export class LwClientsComponent implements OnInit, AfterViewInit , OnDestroy{
    dataSource: MatTableDataSource<any>;
    displayedColumns: string[] = ['Client Endpoint', 'Registration ID', 'Registration Date', 'Last Update', 'Action'];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    clientsArray: any[] = [];
    dataModelComponent: Boolean = false;
    clientsCounter: number;
    clientEndpoint: string;
    myData: any;
    eventArray = [];
    sse: EventSource = new EventSource(this.lwService.lwm2mBaseUri + '/event');
    constructor(private http: HttpClient, private lwService: LwService) { }
    ngOnDestroy(): void {
        this.sse.close();
    }

    ngAfterViewInit(): void {
        this.getClients();

    }
    ngOnInit(): void {
        let mainThis = this;
        this.sse.addEventListener("REGISTRATION", function (e) {
            console.log('REGISTRATION',JSON.parse(e['data']));
            
            mainThis.clientsArray.push(JSON.parse(e['data']))
            mainThis.eventUpdate();
        }, true)

        this.sse.addEventListener("DEREGISTRATION", function (e) {
            console.log('message data be like : 2')
            let index = mainThis.clientsArray.indexOf(JSON.parse(e['data']));
            if(index != -1) mainThis.clientsArray.splice(index,1);
            mainThis.eventUpdate();
        }, true)

        

        this.sse.addEventListener("COAPLOG", function (e) {
        }, true)


        this.sse.addEventListener("UPDATED", function (e) {
        }, true)




    }

    getClients() {
        // /api/clients
        this.http.get<any[]>(this.lwService.lwm2mBaseUri + '/api/clients', { withCredentials: true }).toPromise().then((clientsData) => {
            this.dataSource = new MatTableDataSource(clientsData)
            this.dataSource.paginator = this.paginator;
            this.clientsCounter = clientsData.length;
            this.clientsArray = clientsData;
            console.log('data format :', this.dataSource);
           
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

    eventUpdate(){
            this.dataSource.data = this.clientsArray;
    }


}