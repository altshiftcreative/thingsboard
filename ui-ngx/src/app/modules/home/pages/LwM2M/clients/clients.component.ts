import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit } from "@angular/core";

@Component({
    selector: 'Lw-clients',
    templateUrl: './clients.component.html',
    styleUrls: ['./clients.component.scss']
})


export class LwClientsComponent implements OnInit, AfterViewInit {

    constructor(private http: HttpClient) { }

    ngAfterViewInit(): void {
        this.getClients();
        
    }
    ngOnInit(): void {
        
    }

    getClients(){
        
        this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clients', { withCredentials: true }).subscribe((clientsData) => {
            console.log('clients Data : '+JSON.stringify(clientsData));
            console.log('clients Data : '+clientsData.length);
            
        })
    }
}