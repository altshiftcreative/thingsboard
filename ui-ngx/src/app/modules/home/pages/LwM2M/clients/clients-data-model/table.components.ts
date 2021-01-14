import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, Input, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { LwService } from "../../Lw-service";
import { newInstanceDialog } from "./createInstance /newInstance-dialog.component";

@Component({
    selector: 'Lw-clients-data-model-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.scss']
})


export class LwClientsDataTableComponent implements OnInit, AfterViewInit {
    @Input() dataModel: object;
    @Input() instanceObject: object;
    @Input() instanceNumber: number;
    @Input() timeOut: string;
    @Input() format: string;
    readDataObject: any;
    observeDataObject: any;
    data: any = {};
    dataSource: any[];
    clientByEndpoint: any = {};
    constructor(private lwService: LwService, private http: HttpClient, public dialog: MatDialog) { }

    ngAfterViewInit(): void {
    }
    ngOnInit(): void {

    }

    openDialog() {
        this.lwService.value = this.dataModel['id'];
        this.lwService.format = this.format;
        this.lwService.timeout = this.timeOut;
        this.dialog.open(newInstanceDialog, {
            height: '483px',
            width: '768px',
        }).afterClosed().toPromise().then(async (clientsData) => {
           await this.dynamicRender();
        })



    }

    async dynamicRender(){
        await this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clientsData/?endpoint=' + this.lwService.clientEndpoint, { withCredentials: true }).toPromise().then((clientData) => {
            this.dataSource = clientData
        })
        await this.http.get<any[]>('http://localhost:8080/api/v1/Lw/clientsByEndpoint/?endpoint=' + this.lwService.clientEndpoint, { withCredentials: true }).toPromise().then((clientDataEndpoint) => {
            this.dataSource.forEach(element => {
                let urlArray = []
                clientDataEndpoint['objectLinks'].forEach(item => {
                    if (item['url'].includes("/" + element['id'] + "/")) {
                        let indexOfDash = item['url'].lastIndexOf("/");
                        let final = item['url'].substring(indexOfDash + 1);
                        urlArray.push(parseInt(final));
                        this.clientByEndpoint[element['id']] = urlArray;
                    }
                })
            })
        })
        this.instanceObject = this.clientByEndpoint;
    }

    async readData(value, index, instance) {
        let v = [this.dataModel['id'], instance, value]

        await this.http.get<any[]>('http://localhost:8080/api/v1/Lw/read/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut).toPromise().then((readData) => {
            this.readDataObject = readData;
        })

        if (this.readDataObject['failure']) {
            this.lwService.progress(this.readDataObject['status'], false);
        }
        else {
            this.data['field' + instance + index] = this.readDataObject['content']['value'];
            this.lwService.progress("SUCCESS", true);

        }
    }

    async readAllData(instance) {
        let v = [this.dataModel['id'], instance];
        await this.http.get<any[]>('http://localhost:8080/api/v1/Lw/read/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut).toPromise().then((readData) => {
            this.readDataObject = readData;
        })
        this.readDataObject['content']['resources'].forEach(element => {
            this.data['field' + instance + element['id']] = element['value'];
        });
        this.lwService.progress("SUCCESS", true);
    }

    async writeData(value, instance) {
        let v = [this.dataModel['id'], instance, value];


        let writeValue = prompt("Update resource" + this.dataModel['name']);
        if (writeValue != null && writeValue != "") {
            await this.http.put('http://localhost:8080/api/v1/Lw/write/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut,

                {
                    "id": value,
                    "value": parseInt(writeValue)
                }
            ).subscribe((writeData) => {
            })

            if (this.readDataObject['failure']) {
                this.lwService.progress(this.readDataObject['status'], false);
            }
            else {
                this.lwService.progress("SUCCESS", true);

            }
        }
    }

    async startObserve(value, index, instance) {
        let v = [this.dataModel['id'], instance, value];

        await this.http.post('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut, {}
        ).toPromise().then((observeData) => {
            this.observeDataObject = observeData;

        })

        if (this.observeDataObject['failure']) {
            this.lwService.progress(this.observeDataObject['status'], false);
        }
        else {
            this.data['field' + instance + index] = this.observeDataObject['content']['value'];
            this.lwService.progress("STARTED", true);
        }
    }



    async startObserveAll(instance) {
        let v = [this.dataModel['id'], instance];
        await this.http.post('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut, {}
        ).toPromise().then((observeData) => {
            this.observeDataObject = observeData;
        })

        this.observeDataObject['content']['resources'].forEach(element => {
            this.data['field' + instance + element['id']] = element['value'];
        });
        this.lwService.progress("STARTED", true);
    }



    async stopObserve(value, instance) {
        let v = [this.dataModel['id'], instance, value];

        await this.http.delete('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v, {}
        ).toPromise().then((observeData) => {
            this.lwService.progress('STOPED', true);
        })
    }

    async stopObserveAll(instance) {
        let v = [this.dataModel['id'], instance];
        await this.http.delete('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v, {}
        ).toPromise().then((observeData) => {
            this.lwService.progress('STOPED', true);
        })
    }

    async execute(value, instance) {
        let v = [this.dataModel['id'], instance, value];
        await this.http.post('http://localhost:8080/api/v1/Lw/execute/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&timeout=' + this.timeOut, {}
        ).toPromise().then((executeData) => {
            if (executeData['failure'])
                this.lwService.progress(executeData['status'], false);
            else
                this.lwService.progress("EXECUTE", true);

        })
    }


    async deleteInstance(instance) {
        let confirmation = confirm('Deleting instance. Are you sure?');
        if (confirmation == true) {
            let v = [this.dataModel['id'], instance];
            await this.http.delete('http://localhost:8080/api/v1/Lw/instance/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&timeout=' + this.timeOut, {}
            ).toPromise().then((observeData) => {
                this.lwService.progress('DELETED', true);
            })
            this.dynamicRender();
        }
    }

}