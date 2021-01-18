import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit, ViewEncapsulation } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";
import { LwService } from "../../Lw-service";

@Component({
    selector: 'Lw-certificate',
    templateUrl: './certificate.component.html',
    styleUrls: ['./certificate.component.scss'],

})


export class LwCertificateomponent implements OnInit, AfterViewInit {

    constructor(private http: HttpClient, public dialog: MatDialog,private lwService: LwService) { }
    displayedColumns: string[] = ['Hex', 'Base64'];
    datacertificate;
    hexCode;
    ngOnInit(): void {
        
    }
    ngAfterViewInit() {
        this.getDevices();
    }

    getDevices() {
        this.http.get<any[]>(this.lwService.lwm2mBaseUri+'/api/v1/Lw/certificate', { withCredentials: true }).subscribe((certificateData) => {
            this.datacertificate = certificateData;
            this.hexCode=this.base64ToHex(this.datacertificate.certificate.b64Der);
        })
    }


     base64ToHex(str) {
        const raw = atob(str);
        let result = '';
        for (let i = 0; i < raw.length; i++) {
          const hex = raw.charCodeAt(i).toString(16);
          result += (hex.length === 2 ? hex : '0' + hex);
        }
        return result.toUpperCase();
      }


}
