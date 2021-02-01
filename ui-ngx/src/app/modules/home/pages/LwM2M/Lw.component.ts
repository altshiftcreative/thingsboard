import { Component, OnInit, ViewEncapsulation } from '@angular/core';
@Component({
    selector: 'tb-Lw',
    templateUrl: './Lw.component.html',
    styleUrls: ['./Lw.component.scss'],
    encapsulation: ViewEncapsulation.None

})
export class LwComponent implements OnInit {
    ngOnInit(): void { }

    tabIndex = 0;
    changeTab(event) {
        this.tabIndex = event.index;
    }
}


