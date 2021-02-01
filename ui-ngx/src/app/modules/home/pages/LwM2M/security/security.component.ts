import { AfterViewInit, Component, OnInit, ViewEncapsulation } from "@angular/core";

@Component({
    selector: 'Lw-security',
    templateUrl: './security.component.html',
    styleUrls: ['./security.component.scss'],
    encapsulation: ViewEncapsulation.None

})


export class LwSecurityComponent implements OnInit, AfterViewInit {
    ngAfterViewInit(): void { }
    ngOnInit(): void { }

    tabIndex = 0;
    changeTab(event) {
        this.tabIndex = event.index;
    }
}
