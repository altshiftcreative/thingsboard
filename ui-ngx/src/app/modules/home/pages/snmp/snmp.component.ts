import { HttpClient,HttpParams } from '@angular/common/http';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormControl, FormGroup, Validators,} from '@angular/forms';
const baseURL = '/snmp';


@Component({
    selector: 'snmp',
    templateUrl: './snmp.component.html',
    styleUrls: ['./snmp.component.scss'],
    encapsulation: ViewEncapsulation.None

})
export class SnmpComponent implements OnInit {
    constructor(private http: HttpClient) { }
    setForm: FormGroup;
    snmpValue:any;
    ngOnInit(): void { 
        this.setForm = new FormGroup({

            'host': new FormControl(null, Validators.required),
            'community': new FormControl(null),
            'OID': new FormControl(null),
            'val': new FormControl(null),
        });


    }

    onSubmit() {

        const params = new HttpParams().set('host', this.setForm.value.host)
        .set('community', this.setForm.value.community)
        .set('OID', this.setForm.value.OID)
        .set('val', this.setForm.value.val);
       const fullURL = `${baseURL}?${params.toString()}`;
        console.log({ fullURL });
        this.http.post<any>(fullURL,
        { 
           
        }
    ).subscribe((dta) => {})
    
    }

    onSubmit2() {

        const params = new HttpParams().set('host', this.setForm.value.host)
        .set('community', this.setForm.value.community)
        .set('OID', this.setForm.value.OID)
       const fullURL = `${baseURL}?${params.toString()}`;
        console.log({ fullURL });
     this.http.get<any>(fullURL).subscribe((dta) => {
         console.log(dta)
         this.snmpValue=dta;
     })

    }

}


