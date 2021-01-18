import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { element } from "protractor";
import { LwService } from "../../Lw-service";


@Component({
    selector: 'Lw-clients-data-model',
    templateUrl: './data-model.component.html',
    styleUrls: ['./data-model.component.scss']
})


export class LwClientsDataComponent implements OnInit, AfterViewInit {
    selectedTime = '5';
    selectedMulti = 'TLV';
    selectedSingle = 'TLV';
    dataModelComponent: Boolean;
    instanceNumber = 0;
    dataSource: any[];
    col: number = 0;
    clientByEndpoint: any={};
    
    panelOpenState = false;
    constructor(private lwService: LwService, private http: HttpClient) { }
    clientEndpoint = this.lwService.clientEndpoint;
    ngAfterViewInit(): void {
        this.getDataModel();
        this.getDataModelByEndpoint();
    }
    ngOnInit(): void {

    }

    async getDataModel() {
        await this.http.get<any[]>(this.lwService.lwm2mBaseUri+'/api/v1/Lw/clientsData/?endpoint=' + this.clientEndpoint, { withCredentials: true }).toPromise().then((clientData) => {
            this.dataSource = clientData
            console.log('data testing :',clientData);
            
        })
    }
    async getDataModelByEndpoint() {
        await this.http.get<any[]>(this.lwService.lwm2mBaseUri+'/api/v1/Lw/clientsByEndpoint/?endpoint=' + this.clientEndpoint, { withCredentials: true }).toPromise().then((clientDataEndpoint) => {
            this.dataSource.forEach(element=>{
                let urlArray = []
                clientDataEndpoint['objectLinks'].forEach(item=>{
                     if(item['url'].includes("/"+element['id']+"/")){
                         let indexOfDash = item['url'].lastIndexOf("/");
                         let final = item['url'].substring(indexOfDash+1);
                        urlArray.push(parseInt(final) );
                        this.clientByEndpoint[element['id']] = urlArray;
                     }
                })
            })
        })
    }


}
