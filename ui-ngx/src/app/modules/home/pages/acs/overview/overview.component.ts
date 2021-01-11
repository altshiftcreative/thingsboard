
import { AfterViewInit, Component, OnInit, } from '@angular/core';
import {  ChartType } from 'chart.js';
import { MultiDataSet, Label ,Color} from 'ng2-charts';
import { AcsService } from '../acs-service';
import { HttpClient } from '@angular/common/http';


@Component({
  selector: 'asc-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})

export class AcsOverview implements OnInit,AfterViewInit{

  constructor(private acsService: AcsService,private http: HttpClient) { }

  ngOnInit(): void {
  }

  ngAfterViewInit() {
    this.http.get<any[]>('http://localhost:8080/api/v1/tr69/devices', { withCredentials: true }).subscribe((deviceData) => {
       this.acsService.statusCounter(deviceData); 
       this.doughnutChartData = [
        [this.acsService.online_devices, this.acsService.past_devices, this.acsService.others_devices]
      ]
      this.totalDevices = deviceData.length;
    })
   

}
  totalDevices: number;
  doughnutChartLabels: Label[] = ['Online now', 'Past 24 hours', 'Others']
  doughnutChartData: MultiDataSet = [];
  doughnutChartType: ChartType = 'doughnut';
  

  colors: Color[] = [
    {
      backgroundColor: [
        'rgba(56,158,13,1)',
        'rgba(149,222,100,1)',
        'rgba(217,247,190,1)',
      ]
    }
  ];
  public barChartOptions:any = {
    legend: {position: 'bottom'},
  }  

}