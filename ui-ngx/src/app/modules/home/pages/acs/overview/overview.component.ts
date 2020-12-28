
import { Component, OnInit, } from '@angular/core';
import {  ChartType } from 'chart.js';
import { MultiDataSet, Label ,Color} from 'ng2-charts';

@Component({
  selector: 'asc-overview',
  templateUrl: './overview.component.html',
  styleUrls: ['./overview.component.scss']
})

export class AcsOverview implements OnInit{
  ngOnInit(): void {
    // alert('hasan overview')
  }
  hasan(){
    alert(this.doughnutChartLabels)

  }
  doughnutChartLabels: Label[] = ['Online now', 'Past 24 hours', 'Others'];  
  doughnutChartData: MultiDataSet = [
    [50, 50, 0]
  ];
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