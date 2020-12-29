import { Component, OnInit, ViewChild, AfterViewInit } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { AcsService } from '../acs-service';

@Component({
    selector: 'dialog-data-example-dialog',
    templateUrl: 'dialog-data.html',
})

export class DialogDataDialog implements OnInit, AfterViewInit {

    @ViewChild(MatPaginator) paginator: MatPaginator;

    myDataSouce: MatTableDataSource<any>

    constructor(private acsService: AcsService) { }
    displayedColumns: string[] = ['Parameter', 'Value', 'Action'];
    ngOnInit(): void {
        this.myDataSouce = new MatTableDataSource(this.acsService.deviceArrayData);
        this.myDataSouce.paginator = this.paginator;

    }
    ngAfterViewInit(): void {
        this.myDataSouce.paginator = this.paginator;

    }

    updateValue(parameterName, value) {
        let newValue = prompt(parameterName, value);
        let deviceID = this.acsService.deviceArrayData[0].deviceData['value'][0];
        this.acsService.change(deviceID, parameterName, newValue);
    };

    refreshValue(parameterName) {
        let deviceID = this.acsService.deviceArrayData[0].deviceData['value'][0];
        this.acsService.refresh(deviceID, parameterName);
    }

    liveSearchParameter(event){
        if(event.target.value == "") this.myDataSouce.data = this.acsService.deviceArrayData;
        else{let arrayContainer=[];
            this.acsService.deviceArrayData.forEach((element)=>{
                if(element.parameter.toLowerCase().includes(event.target.value.toLowerCase())){
                    arrayContainer.push(element);
                }
            })
            this.myDataSouce.data = arrayContainer;
        }
    }

    addInstance(element){
        let parameterName = element.parameter;
        let id = element['deviceData']['value'][0];
        this.acsService.addInstance(id,parameterName)
    }
}