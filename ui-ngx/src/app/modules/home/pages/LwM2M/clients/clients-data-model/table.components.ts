import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, Input, OnInit } from "@angular/core";
import { LwService } from "../../Lw-service";

@Component({
    selector: 'Lw-clients-data-model-table',
    templateUrl: './table.component.html',
    styleUrls: ['./table.component.scss']
})


export class LwClientsDataTableComponent implements OnInit, AfterViewInit {
    @Input() dataModel: object;
    @Input() instanceNumber: number;
    @Input() timeOut: string;
    @Input() format: string;
    readDataObject: any;
    observeDataObject: any;
    data: any = {};
    constructor(private lwService: LwService, private http: HttpClient) { }

    ngAfterViewInit(): void {

    }
    ngOnInit(): void {

    }


    async readData(value, index) {
        let v = [this.dataModel['id'], this.instanceNumber, value]

        await this.http.get<any[]>('http://localhost:8080/api/v1/Lw/read/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut).toPromise().then((readData) => {
            console.log('read Response : ', readData);
            this.readDataObject = readData;
        })

        if (this.readDataObject['failure']) {
            this.lwService.progress(this.readDataObject['status'], false);
        }
        else {
            this.data['field' + index] = this.readDataObject['content']['value'];
            this.lwService.progress(this.readDataObject['status'], true);

        }
    }

    async readAllData() {
        let v = [this.dataModel['id'], this.instanceNumber];
        await this.http.get<any[]>('http://localhost:8080/api/v1/Lw/read/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut).toPromise().then((readData) => {
            console.log('read all data Response : ', readData['content']['resources']);
            this.readDataObject = readData;
        })
        this.readDataObject['content']['resources'].forEach(element => {
            this.data['field' + element['id']] = element['value'];
        });
        this.lwService.progress(this.readDataObject['status'], true);
    }

    async writeData(value) {
        let v = [this.dataModel['id'], this.instanceNumber, value];


        let writeValue = prompt("Update resource" + this.dataModel['name']);
        if (writeValue != null && writeValue != "") {
            await this.http.put('http://localhost:8080/api/v1/Lw/write/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut,

                {
                    "id": value,
                    "value": parseInt(writeValue)
                }
            ).subscribe((writeData) => {
                console.log('write Response : ', writeData);
            })

            if (this.readDataObject['failure']) {
                this.lwService.progress(this.readDataObject['status'], false);
            }
            else {
                this.lwService.progress(this.readDataObject['status'], true);

            }
        }
    }

    async startObserve(value, index) {
        let v = [this.dataModel['id'], this.instanceNumber, value];

        await this.http.post('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut, {}
        ).toPromise().then((observeData) => {
            this.observeDataObject = observeData;

        })

        if (this.observeDataObject['failure']) {
            this.lwService.progress(this.observeDataObject['status'], false);
        }
        else {
            this.data['field' + index] = this.observeDataObject['content']['value'];
            this.lwService.progress(this.observeDataObject['status'], true);
        }
    }



    async startObserveAll() {
        let v = [this.dataModel['id'], this.instanceNumber];
        await this.http.post('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&format=' + this.format + '&timeout=' + this.timeOut, {}
        ).toPromise().then((observeData) => {
            this.observeDataObject = observeData;
        })

        this.observeDataObject['content']['resources'].forEach(element => {
            this.data['field' + element['id']] = element['value'];
        });
        this.lwService.progress(this.observeDataObject['status'], true);
    }



    async stopObserve(value) {
        let v = [this.dataModel['id'], this.instanceNumber, value];

        await this.http.delete('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v, {}
        ).toPromise().then((observeData) => {
            this.lwService.progress('STOPED', true);
        })
    }

    async stopObserveAll() {
        let v = [this.dataModel['id'], this.instanceNumber];
        await this.http.delete('http://localhost:8080/api/v1/Lw/observe/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v, {}
        ).toPromise().then((observeData) => {
            this.lwService.progress('STOPED', true);
        })
    }

    async execute(value) {
        let v = [this.dataModel['id'], this.instanceNumber, value];
        await this.http.post('http://localhost:8080/api/v1/Lw/execute/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&timeout=' + this.timeOut, {}
        ).toPromise().then((executeData) => {
            this.lwService.progress(executeData['status'], false);

        })
    }


    async deleteInstance() {
        let v = [this.dataModel['id'], this.instanceNumber];
        await this.http.delete('http://localhost:8080/api/v1/Lw/instance/?endpoint=' + this.lwService.clientEndpoint + '&value=' + v + '&timeout=' + this.timeOut, {}
        ).toPromise().then((observeData) => {
            this.lwService.progress('DELETED', true);
        })
    }

}