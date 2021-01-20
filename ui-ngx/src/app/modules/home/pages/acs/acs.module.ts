import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { AcsComponent } from '@modules/home/pages/acs/acs.component';
import { DialogDataDialog } from '@modules/home/pages/acs/devices/Dialog.component';
import { AcsRoutingModule } from '@modules/home/pages/acs/acs-routing.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';
import { ChartsModule } from 'ng2-charts';
import { AcsOverview } from './overview/overview.component';
import { AcsDeciveComponent } from './devices/device.component';
import { AcsFaultsComponent } from './faults/faults.component';
import { AcsAdminComponent } from './admin/admin.component';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { AcsAdminConfigComponent } from './admin/Config/admin-config.component';
import { AcsAdminPresetsComponent } from './admin/Presets/admin-presets.component';
import { AcsAdminProvisionsComponent } from './admin/Provisions/admin-provisions.component';
import { PresetsDialog } from './admin/Presets/presets-dialog.component';
import { DialogAlert} from './popup/popup-show';
import { ReactiveFormsModule } from '@angular/forms';
import { provisionsDialog } from './admin/Provisions/provisions-dialog.component';
import { configDialog } from './admin/Config/config-dialog.component';
import { MatSnackBarModule } from '@angular/material/snack-bar';


@NgModule({
  declarations: [
   AcsComponent,
   DialogDataDialog,
   AcsDeciveComponent,
   AcsOverview,
   AcsFaultsComponent,
   AcsAdminComponent,
   AcsAdminConfigComponent,
   AcsAdminPresetsComponent,
   AcsAdminProvisionsComponent,
   PresetsDialog,
   DialogAlert,
   provisionsDialog,
   configDialog,
   
   

  ],
  imports: [
    ChartsModule,
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    AcsRoutingModule,
    MatButtonToggleModule,
    ReactiveFormsModule,
    MatSnackBarModule

    
  ],
  exports:[
    ReactiveFormsModule
  ]
  
})
export class ACSModule { }
