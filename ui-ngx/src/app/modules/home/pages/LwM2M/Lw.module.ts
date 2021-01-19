
import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';
import { ChartsModule } from 'ng2-charts';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { LwRoutingModule } from './Lw-routing.module';
import { LwComponent } from './Lw.component';
import { LwClientsComponent } from './clients/clients.component';
import { LwSecurityComponent } from './security/security.component';
import { LwClientsDataComponent } from './clients/clients-data-model/data-model.component';
import { LwCertificateomponent } from './security/Certificate/certificate.component';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { LwClientsDataTableComponent } from './clients/clients-data-model/table.components';
import { LwPublicKeycateomponent } from './security/Public Key/publicKey.component';
import { LwClientSecurityConfigComponent } from './security/Client Security Config/clientSecurityConfig.component';
import { newConfigDialog } from './security/Client Security Config/newConfig-dialog.component';
import { MatIcon, MatIconModule } from '@angular/material/icon';
import { formDialog } from './clients/global-form/form.component';


@NgModule({
  declarations: [
    LwComponent,
    LwClientsComponent,
    LwSecurityComponent,
    LwClientsDataComponent,
    LwCertificateomponent,
    LwClientsDataComponent,
    LwClientsDataTableComponent,
    LwPublicKeycateomponent,
    LwClientSecurityConfigComponent,
    newConfigDialog,
    formDialog
    
    

  ],
  imports: [
    ChartsModule,
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    LwRoutingModule,
    MatButtonToggleModule,
    ReactiveFormsModule,
    MatSnackBarModule,
    MatTableModule,
    MatSortModule,
    MatIconModule,
   

    
  ],
  exports:[
    ReactiveFormsModule
  ]
  
})
export class LwModule { }
