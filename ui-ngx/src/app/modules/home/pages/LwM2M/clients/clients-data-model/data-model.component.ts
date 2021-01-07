import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { LwService } from "../../Lw-service";


@Component({
    selector: 'Lw-clients-data-model',
    templateUrl: './data-model.component.html',
    styleUrls: ['./data-model.component.scss']
})


export class LwClientsDataComponent implements OnInit, AfterViewInit {
    selectedTime = '5s';
    selectedMulti = 'TLV';
    selectedSingle = 'TLV';
    dataModelComponent: Boolean ;
    instanceNumber = 0;
    dataSource: any[];
    col:number = 0;
    panelOpenState = false;
    constructor(private lwService: LwService,private http: HttpClient) { }
    clientEndpoint = this.lwService.clientEndpoint;
    ngAfterViewInit(): void {
        this.getDataModel();
    }
    ngOnInit(): void {
    
    }

    getDataModel(){
        this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clientsData/?endpoint='+this.clientEndpoint, { withCredentials: true }).subscribe((clientData) => {
           console.log('clients Dataaaaaa : ',clientData);
           this.dataSource = clientData
        })
    }

    
}
