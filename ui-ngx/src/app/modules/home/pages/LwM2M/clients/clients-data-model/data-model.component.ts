import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnDestroy, OnInit } from "@angular/core";
import { LwService } from "../../Lw-service";


@Component({
    selector: 'Lw-clients-data-model',
    templateUrl: './data-model.component.html',
    styleUrls: ['./data-model.component.scss']
})


export class LwClientsDataComponent implements OnInit, AfterViewInit, OnDestroy {
    selectedTime = '5';
    selectedMulti = 'TLV';
    selectedSingle = 'TLV';
    dataModelComponent: Boolean;
    instanceNumber = 0;
    dataSource: any[];
    col: number = 0;
    clientByEndpoint: any = {};
    panelOpenState = false;
    clientEndpoint = this.lwService.clientEndpoint;
    sse: EventSource = new EventSource(this.lwService.lwm2mBaseUri + '/event?ep=' + this.lwService.clientEndpoint);
    data: any = {};
    counterArray = [];
    updateArray = [];
    constructor(private lwService: LwService, private http: HttpClient) { }

    ngOnDestroy(): void {
        console.log('destroooy');
        this.sse.close();
    }

    ngAfterViewInit(): void {
        this.getDataModel();
    }
    ngOnInit(): void {
        let mainThis = this;

        this.sse.addEventListener("NOTIFICATION", function (e) {
            // console.log('message data be like : 3', JSON.parse(e['data']))

            let notificationData = JSON.parse(e['data'])['val']['resources'];

            if (notificationData) {
                let instance = JSON.parse(e['data'])['res']
                let indexOfDash = instance.lastIndexOf("/");
                let final = instance.substring(indexOfDash + 1)

                let n = instance.split('/', 2).join('/').length;
                let objectID = instance.substring(1, n);

                notificationData.forEach(e => {
                    mainThis.data['field'+objectID + parseInt(final) + e['id']] = e['value'];
                })
            }
            else {
                let instance = JSON.parse(e['data'])['res']
                let n = instance.split('/', 2).join('/').length;
                let l = instance.split('/', 3).join('/').length;
                let final = instance.substring(n + 1, l)

                let objectID = instance.substring(1, n);

                notificationData = JSON.parse(e['data'])['val'];
                mainThis.data['field'+objectID + parseInt(final) + notificationData['id']] = notificationData['value'];

            }

        }, true)

        this.sse.addEventListener("UPDATED", function (e) {
            JSON.parse(e['data'])['registration']['objectLinks'].forEach(element => {
                let sub = element['url'].match("/(.*)/");
                if (sub != null && !mainThis.updateArray.includes(sub[1])) {
                    mainThis.updateArray.push(sub[1]);
                }
            });
            if (mainThis.updateArray.length != mainThis.counterArray.length) {
                mainThis.getDataModel();
            }

        }, true)
    }

    getDataModel() {
        this.http.get<any[]>(this.lwService.lwm2mBaseUri + '/api/objectspecs/' + this.clientEndpoint, { withCredentials: true }).toPromise().then((clientData) => {
            clientData.sort((a, b) => (a.id > b.id) ? 1 : -1)
            this.dataSource = clientData
            this.getDataModelByEndpoint();

        })
    }
    getDataModelByEndpoint() {
        this.http.get<any[]>(this.lwService.lwm2mBaseUri + '/api/clients/' + this.clientEndpoint, { withCredentials: true }).toPromise().then((clientDataEndpoint) => {
            this.dataSource.forEach(element => {
                let urlArray = []
                clientDataEndpoint['objectLinks'].forEach(item => {
                    if (item['url'].includes("/" + element['id'] + "/")) {
                        let indexOfDash = item['url'].lastIndexOf("/");
                        let final = item['url'].substring(indexOfDash + 1);
                        urlArray.push(parseInt(final));
                        this.clientByEndpoint[element['id']] = urlArray;
                    }
                    let sub = item['url'].match("/(.*)/");
                    if (sub != null && !this.counterArray.includes(sub[1])) {
                        this.counterArray.push(sub[1]);
                    }
                })
            })
        })
    }

}
