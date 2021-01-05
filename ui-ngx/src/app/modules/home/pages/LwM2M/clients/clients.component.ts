import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit } from "@angular/core";

@Component({
    selector: 'Lw-clients',
    templateUrl: './clients.component.html',
    styleUrls: ['./clients.component.scss']
})


export class LwClientsComponent implements OnInit, AfterViewInit {
    clientsCounter: number ;
    constructor(private http: HttpClient) { }

    ngAfterViewInit(): void {
        this.getClients();
        
    }
    ngOnInit(): void {
        
    }

    getClients(){
        
        this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clients', { withCredentials: true }).subscribe((clientsData) => {
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