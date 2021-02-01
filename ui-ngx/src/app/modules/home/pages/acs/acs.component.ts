import { Component, OnInit, ViewEncapsulation } from '@angular/core';
@Component({
    selector: 'tb-acs',
    templateUrl: './acs.component.html',
    styleUrls: ['./acs.component.scss'],
    encapsulation: ViewEncapsulation.None

})
export class AcsComponent implements OnInit {
    ngOnInit(): void { }
    tabIndex = 0;
    changeTab(event) {
        this.tabIndex = event.index;
    }
}


