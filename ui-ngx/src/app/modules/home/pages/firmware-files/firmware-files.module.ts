import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '@shared/shared.module';
import { FirmwareFilesRoutingModule } from '@modules/home/pages/firmware-files/firmware-files-routing.module';
import { HomeComponentsModule } from '@modules/home/components/home-components.module';
import { ChartsModule } from 'ng2-charts';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { FirmwareFilesComponent } from './firmware-files.component';


@NgModule({
  declarations: [
    FirmwareFilesComponent
  ],
  imports: [
    ChartsModule,
    CommonModule,
    SharedModule,
    HomeComponentsModule,
    FirmwareFilesRoutingModule,
    MatButtonToggleModule,
    ReactiveFormsModule,
    MatSnackBarModule

    
  ],
  exports:[
    ReactiveFormsModule
  ]
  
})
export class FirmwareModule { }
