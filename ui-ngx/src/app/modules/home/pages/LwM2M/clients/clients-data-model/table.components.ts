import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, Input, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { LwService } from "../../Lw-service";
import { formDialog } from "../global-form/form.component";

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

    ngAfterViewInit(): void { }
    ngOnInit(): void { }

    openDialog() {
        this.lwService.value = this.dataModel['id'];
        this.lwService.format = this.format;
        this.lwService.timeout = this.timeOut;
        this.lwService.formType = { type: 'create', name: 'Create Instance' };

        for (const element of this.dataModel['resourcedefs']) {
            if (element['operations'] == 'W' || element['operations'] == 'RW') {
                this.lwService.formData.push(element);
            }
        }

        this.dialog.open(formDialog, {
            height: '483px',
            width: '768px',
        }).afterClosed().toPromise().then(async (clientsData) => {
            await this.dynamicRender();
            this.lwService.formData = [];
        })



    }

    async dynamicRender() {
        await this.http.get<any[]>(this.lwService.lwm2mBaseUri + '/api/objectspecs/' + this.lwService.clientEndpoint, { withCredentials: true }).toPromise().then((clientData) => {
            this.dataSource = clientData
        })
        await this.http.get<any[]>(this.lwService.lwm2mBaseUri + '/api/clients/' + this.lwService.clientEndpoint, { withCredentials: true }).toPromise().then((clientDataEndpoint) => {
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

        await this.http.get<any[]>(this.lwService.lwm2mBaseUri + '/api/clients/' + this.lwService.clientEndpoint + '/' + this.dataModel['id'] + '/' + instance + '/' + value + '?format=' + this.format + '&timeout=' + this.timeOut).toPromise().then((readData) => {
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
        await this.http.get<any[]>(this.lwService.lwm2mBaseUri + '/api/clients/' + this.lwService.clientEndpoint + '/' + this.dataModel['id'] + '/' + instance + '?format=' + this.format + '&timeout=' + this.timeOut).toPromise().then(async (readData) => {
            this.readDataObject = readData;

            for await (const element of this.readDataObject['content']['resources']) {
                this.data['field' + instance + element['id']] = element['value'];
            }
            this.lwService.progress("SUCCESS", true);
        })

    }

    async writeData(value, index, instance) {
        this.lwService.formType = { type: 'write', name: 'Update resource ' + this.dataModel['resourcedefs'][index]['name'], 'id': index };
        let writeObject = { 'id': value, 'mandatory': true, 'name': this.dataModel['resourcedefs'][index]['name'] }
        this.lwService.formData.push(writeObject)

        this.lwService.value = [this.dataModel['id'], instance, value];
        this.lwService.format = this.format;
        this.lwService.timeout = this.timeOut;

        this.dialog.open(formDialog, {
            height: '483px',
            width: '768px',
        }).afterClosed().toPromise().then(async (clientsData) => {
            await this.dynamicRender();
            this.lwService.formData = [];
            this.data['field' + instance + index] = this.lwService.finalWriteValue;
        })
    }

    async startObserve(value, index, instance) {
        
        await this.http.post(this.lwService.lwm2mBaseUri + '/api/clients/' + this.lwService.clientEndpoint + '/' + this.dataModel['id'] + '/' + instance + '/' + value + '/observe?format=' + this.format + '&timeout=' + this.timeOut, {}
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
        await this.http.post(this.lwService.lwm2mBaseUri + '/api/clients/' + this.lwService.clientEndpoint + '/' + this.dataModel['id'] + '/' + instance + '/observe?format=' + this.format + '&timeout=' + this.timeOut, {}
        ).toPromise().then((observeData) => {
            this.observeDataObject = observeData;
        })

        this.observeDataObject['content']['resources'].forEach(element => {
            this.data['field' + instance + element['id']] = element['value'];
        });
        this.lwService.progress("STARTED", true);
    }



    async stopObserve(value, instance) {
        

        await this.http.delete(this.lwService.lwm2mBaseUri + '/api/clients/' + this.lwService.clientEndpoint + '/' + this.dataModel['id'] + '/' + instance + '/' + value + '/observe', {}
        ).toPromise().then((observeData) => {
            this.lwService.progress('STOPED', true);
        })
    }

    async stopObserveAll(instance) {
        await this.http.delete(this.lwService.lwm2mBaseUri + '/api/clients/' + this.lwService.clientEndpoint + '/' + this.dataModel['id'] + '/' + instance + '/observe', {}
        ).toPromise().then((observeData) => {
            this.lwService.progress('STOPED', true);
        })
    }

    async execute(value, instance) {
        
        await this.http.post(this.lwService.lwm2mBaseUri + "/api/clients/" + this.lwService.clientEndpoint + "/" + this.dataModel['id'] + "/" + instance + "/" + value + "?timeout=" + this.timeOut, {}
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

            await this.http.delete(this.lwService.lwm2mBaseUri + "/api/clients/" + this.lwService.clientEndpoint + "/" + this.dataModel['id'] + "/" + instance + "?timeout=" + this.timeOut, {}
            ).toPromise().then((observeData) => {
                this.lwService.progress('DELETED', true);
            })
            this.dynamicRender();
        }
    }


    updateInstance(instance) {
        this.lwService.value = [this.dataModel['id'], instance];
        this.lwService.format = this.format;
        this.lwService.timeout = this.timeOut;
        this.lwService.formType = { type: 'update', name: 'Update Instance' };

        for (const element of this.dataModel['resourcedefs']) {
            if (element['operations'] == 'W' || element['operations'] == 'RW') {
                this.lwService.formData.push(element);
            }
        }
        if (this.lwService.formData.length > 0) {
            this.dialog.open(formDialog, {
                height: '483px',
                width: '768px',
            }).afterClosed().toPromise().then(async (clientsData) => {
                await this.dynamicRender();
                this.lwService.formData = [];
            })
        }
    }

}