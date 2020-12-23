import { Component, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, ElementRef } from '@angular/core';
@Component({
    selector: 'tb-acs',
    templateUrl: './acs.component.html',
    styleUrls: ['./acs.component.scss']
})
export class AcsComponent {

    tabIndex = 0;
    changeTab(event) {
        console.log(event.index)
        this.tabIndex = event.index;
    }
}


