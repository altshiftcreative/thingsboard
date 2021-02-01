
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {AcsComponent} from './acs.component'
import { TenantsTableConfigResolver } from '@modules/home/pages/tenant/tenants-table-config.resolver';


const routes: Routes = [
  {
    path: 'acs',
    component: AcsComponent,
    data: {
      breadcrumb: {
        label: 'TR-69',
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
export class AcsRoutingModule { }
