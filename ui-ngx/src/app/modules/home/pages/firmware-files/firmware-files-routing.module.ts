import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {FirmwareFilesComponent} from './firmware-files.component'
import { TenantsTableConfigResolver } from '@modules/home/pages/tenant/tenants-table-config.resolver';


const routes: Routes = [
  {
    path: 'firmware',
    component: FirmwareFilesComponent,
    data: {
      breadcrumb: {
        label: 'Firmware',
        icon: 'cloud_queue'
      }
    }
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
  providers: [
    TenantsTableConfigResolver
  ]
})
export class FirmwareFilesRoutingModule { }
