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
    walkData: Object ;

    setForm: FormGroup;
    getForm: FormGroup;
    walkForm: FormGroup;

    snmpValue:any;
    
    ngOnInit(): void { 
        this.setForm = new FormGroup({

            'host': new FormControl(null, Validators.required),
            'community': new FormControl(null),
            'OID': new FormControl(null),
            'val': new FormControl(null),
        });
        this.walkForm = new FormGroup({

            'host': new FormControl(null, Validators.required),
            'community': new FormControl(null),
            'OID': new FormControl(null),
            'val': new FormControl(null),
        });
        this.getForm = new FormGroup({

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
        this.http.post(fullURL,{responseType: 'text'}).subscribe((dta) => {})
    
    }

    onSubmit2() {

        const params = new HttpParams().set('host', this.getForm.value.host)
        .set('community', this.getForm.value.community)
        .set('OID', this.getForm.value.OID)
       const fullURL = `${baseURL}?${params.toString()}`;
        console.log({ fullURL });
     this.http.get(fullURL,{responseType: 'text'}).subscribe((dta) => {
         console.log(dta)
         this.snmpValue=dta;
        
     })

    }
    
    onSubmit3() {

        const params = new HttpParams().set('host', this.walkForm.value.host)
        .set('community', this.walkForm.value.community)
        .set('OID', this.walkForm.value.OID)
       const fullURL = `snmpWalk?${params.toString()}`;
        console.log({ fullURL });
     this.http.get(fullURL).subscribe((dta) => {
         this.walkData=dta
         console.log(this.walkData)
         
     })

     this.setForm = new FormGroup({

        'host': new FormControl(this.walkForm.value.host, Validators.required),
        'community': new FormControl(this.walkForm.value.community),
        'OID': new FormControl(null),
        'val': new FormControl(null),
    });
    this.getForm = new FormGroup({

        'host': new FormControl(this.walkForm.value.host, Validators.required),
        'community': new FormControl(this.walkForm.value.community),
        'OID': new FormControl(null),
        'val': new FormControl(null),
    });

    }


}

