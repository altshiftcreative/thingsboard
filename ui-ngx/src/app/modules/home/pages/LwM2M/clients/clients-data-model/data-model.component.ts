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
            console.log('message data be like : 3', JSON.parse(e['data'])['val']['resources'])
            let notificationData = JSON.parse(e['data'])['val']['resources'];
            if (notificationData)
                notificationData.forEach(e => {
                    mainThis.data['field' + 0 + e['id']] = e['value'];
                })
        }, true)

        this.sse.addEventListener("UPDATED", function (e) {
            // mainThis.getDataModel();
            JSON.parse(e['data'])['registration']['objectLinks'].forEach(element => {
                let sub = element['url'].match("/(.*)/");
                if (sub != null && !mainThis.updateArray.includes(sub[1])) {
                    mainThis.updateArray.push(sub[1]);
                }
            });

            // console.log('the update data length NEW: ', mainThis.updateArray);
            // console.log('the API data length: ',mainThis.counterArray);
            if(mainThis.updateArray.length != mainThis.counterArray.length){
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
