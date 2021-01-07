import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, Input, OnInit, ViewChild } from "@angular/core";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { LwService } from "../../Lw-service";

@Component({
    selector: 'Lw-clients-data-model-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.scss']
})


export class LwClientsDataTableComponent implements OnInit, AfterViewInit {
    @Input() dataModel: object;
    @Input() instanceNumber: number;
    constructor(private lwService: LwService,private http: HttpClient) { }
   
    ngAfterViewInit(): void {
        
    }
    ngOnInit(): void {
        
    }

    
}