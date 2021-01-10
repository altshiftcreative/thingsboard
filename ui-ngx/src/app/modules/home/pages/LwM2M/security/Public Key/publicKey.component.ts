import { HttpClient } from "@angular/common/http";
import { AfterViewInit, Component, OnInit } from "@angular/core";
import { MatDialog } from "@angular/material/dialog";
import { MatTableDataSource } from "@angular/material/table";

@Component({
    selector: 'Lw-publicKey',
    templateUrl: './publicKey.component.html',
    styleUrls: ['./publicKey.component.scss'],

})


export class LwPublicKeycateomponent implements OnInit, AfterViewInit {

    constructor(private http: HttpClient, public dialog: MatDialog) { }
    displayedColumns: string[] = ['params', 'x','y'];
    datacertificate;
    dataSource: MatTableDataSource<any>;
    hexCode;
    ngOnInit(): void {
        
    }
    ngAfterViewInit() {
        this.getDevices();
    }

    getDevices() {
        this.http.get<any[]>('http://localhost:8080/api/v1/Lw/certificate', { withCredentials: true }).subscribe((certificateData) => {
            this.dataSource = new MatTableDataSource(certificateData);
            console.log(this.dataSource)
            this.datacertificate = certificateData;
            this.hexCode=this.base64ToHex(this.datacertificate.certificate.pubkey.b64Der);
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
