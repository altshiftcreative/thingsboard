import { Component, OnInit, Inject, ViewChild, AfterViewInit, EventEmitter, ElementRef , ViewEncapsulation} from '@angular/core';
@Component({
    selector: 'tb-Lw',
    templateUrl: './Lw.component.html',
    styleUrls: ['./Lw.component.scss'],
    encapsulation: ViewEncapsulation.None

})
export class LwComponent implements OnInit{
    ngOnInit(): void {

}

    tabIndex = 0;
    changeTab(event) {
        console.log(event.index)
        this.tabIndex = event.index;
    }
}


