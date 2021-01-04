///
/// Copyright © 2016-2020 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

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


@NgModule({
  declarations: [
    LwComponent,
    LwClientsComponent,
    LwSecurityComponent
   

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
   

    
  ],
  exports:[
    ReactiveFormsModule
  ]
  
})
export class LwModule { }
