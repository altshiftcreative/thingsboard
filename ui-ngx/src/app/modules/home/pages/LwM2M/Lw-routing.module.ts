import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import {LwComponent} from './Lw.component'
import { TenantsTableConfigResolver } from '@modules/home/pages/tenant/tenants-table-config.resolver';


const routes: Routes = [

  {
    path: 'LwM2M',
    component: LwComponent,
    data: {
      breadcrumb: {
        label: 'LwM2M',
        icon: 'memory'
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
export class LwRoutingModule { }
