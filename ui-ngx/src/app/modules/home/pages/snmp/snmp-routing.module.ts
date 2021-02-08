import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { SnmpComponent } from './snmp.component'
import { TenantsTableConfigResolver } from '@modules/home/pages/tenant/tenants-table-config.resolver';


const routes: Routes = [
  {
    path: 'snmp',
    component: SnmpComponent,
    data: {
      breadcrumb: {
        label: 'SNMP',
        icon: 'router'
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
export class SnmpRoutingModule { }
