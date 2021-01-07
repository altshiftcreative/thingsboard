import { AfterViewInit, Component, OnInit } from "@angular/core";

@Component({
    selector: 'Lw-security',
    templateUrl: './security.component.html',
    styleUrls: ['./security.component.scss']
})


export class LwSecurityComponent implements OnInit, AfterViewInit {
    ngAfterViewInit(): void {
    }
    ngOnInit(): void {
    }

    tabIndex = 0;
    changeTab(event) {
        console.log(event.index)
        this.tabIndex = event.index;
    }
}
