import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
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
    const rows$ = of(exampleShips);
    this.displayedRows$ = rows$;
    }

    getDataModel(){
        this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clientsData/?endpoint='+this.clientEndpoint, { withCredentials: true }).subscribe((clientData) => {
           console.log('clients Dataaaaaa : ',clientData);
           this.dataSource = clientData
        })
    }

    
}
export interface ShipData {
    vesselId: string;
    vesselName: string;
    ircs?: string;
    countryCode: string;
    vesselStatus: string;
    grossTonnage?: number;
    hullNumber?: string;
    vesselType: string;
    jonesActEligible: boolean;
    disabledDate?: string;
  }
  
  export const exampleShips: ShipData[]=[
    {
      'ircs': 'V7QV6',
      'vesselName': 'KYOWA ORCHID',
      'countryCode': 'US',
      'vesselId': 'IMO8675368',
      'vesselStatus': 'S',
      'grossTonnage': 1,
      'hullNumber': undefined,
      'vesselType': 'A21',
      'jonesActEligible': false,
      'disabledDate': undefined
    },
    {
      'ircs': 'V7QV6',
      'vesselName': 'hasan',
      'countryCode': 'US',
      'vesselId': 'IMO8675368',
      'vesselStatus': 'S',
      'grossTonnage': 1,
      'hullNumber': undefined,
      'vesselType': 'A21',
      'jonesActEligible': false,
      'disabledDate': undefined
    },
    {
        'ircs': 'V7QV6',
        'vesselName': 'hasan',
        'countryCode': 'US',
        'vesselId': 'IMO8675368',
        'vesselStatus': 'S',
        'grossTonnage': 1,
        'hullNumber': undefined,
        'vesselType': 'A21',
        'jonesActEligible': false,
        'disabledDate': undefined
      },
      {
        'ircs': 'V7QV6',
        'vesselName': 'hasan',
        'countryCode': 'US',
        'vesselId': 'IMO8675368',
        'vesselStatus': 'S',
        'grossTonnage': 1,
        'hullNumber': undefined,
        'vesselType': 'A21',
        'jonesActEligible': false,
        'disabledDate': undefined
      },
      {
        'ircs': 'V7QV6',
        'vesselName': 'hasan',
        'countryCode': 'US',
        'vesselId': 'IMO8675368',
        'vesselStatus': 'S',
        'grossTonnage': 1,
        'hullNumber': undefined,
        'vesselType': 'A21',
        'jonesActEligible': false,
        'disabledDate': undefined
      },
  ]