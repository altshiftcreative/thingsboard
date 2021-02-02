import { NgModule } from '@angular/core';
import { SnmpComponent } from './snmp.component';
import { SnmpRoutingModule } from './snmp-routing.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatRippleModule } from '@angular/material/core';


@NgModule({
  declarations: [
    SnmpComponent,

   

  ],
  imports: [
    SnmpRoutingModule,
    ReactiveFormsModule,
    FormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatRippleModule,

    
  ],
  exports:[
    ReactiveFormsModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatRippleModule,

]
  
})
export class SnmpModule { }
