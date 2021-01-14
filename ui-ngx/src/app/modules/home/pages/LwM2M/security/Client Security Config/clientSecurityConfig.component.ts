import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewChild } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatPaginator } from "@angular/material/paginator";
import { MatTableDataSource } from "@angular/material/table";
import { LwService } from "../../Lw-service";
import { newConfigDialog } from "./newConfig-dialog.component"

@Component({
    selector: 'Lw-clientSecurityConfig',
    templateUrl: './clientSecurityConfig.component.html',
    styleUrls: ['./clientSecurityConfig.component.scss']
})


export class LwClientSecurityConfigComponent implements OnInit, AfterViewInit {
    dataSource: MatTableDataSource<any>;
    displayedColumns: string[] = ['Client Endpoint', 'Security Mode', 'Security Information', 'Action'];
    @ViewChild(MatPaginator) paginator: MatPaginator;
    clientsArray: any[];
    dataModelComponent: Boolean = false;
    clientsCounter: number;
    clientEndpoint: string;
    constructor(private http: HttpClient, public dialog: MatDialog, private lwService: LwService) { }

    ngAfterViewInit(): void {
        this.getClients();

    }
    ngOnInit(): void {

    }

    openDialog() {
        this.dialog.open(newConfigDialog, {
            height: '450px',
            width: '240px',
        }).afterClosed().subscribe((clientsData) => {
            this.getClients();
        })
    }

    getClients() {

        this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clientsSecurity', { withCredentials: true }).subscribe((clientsData) => {
            this.dataSource = new MatTableDataSource(clientsData)
            this.dataSource.paginator = this.paginator;
            this.clientsCounter = clientsData.length;
            this.clientsArray = clientsData;
            console.log(this.dataSource)

        })
    }
    async deletClients(id) {
        let confirmation = confirm('Deleting client. Are you sure?');
        if (confirmation == true) {
            await this.http.delete('http://localhost:8080/api/v1/Lw/deleteClientsSecurity/?endpoint=' + id).toPromise().then((dta) => { })
            this.getClients();
            this.lwService.progress('DELETED', true);
        }

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



}