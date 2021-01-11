import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppState } from '@app/core/core.state';
import { ActionNotificationShow } from '@app/core/notification/notification.actions';
import { NotificationType } from '@app/core/notification/notification.models';
import { Store } from '@ngrx/store';

@Injectable({
    providedIn: 'root'
})

export class LwService {
    clientEndpoint;
    constructor(private http: HttpClient,protected store: Store<AppState>) { }

    progress(res, stat) {
        let type: NotificationType  = stat ? 'success' : 'error'
            this.store.dispatch(new ActionNotificationShow(
                {
                    message: res,
                    type: type,
                    duration: 2000,
                    verticalPosition: 'bottom',
                    horizontalPosition: 'center'
                }));
        
    }

}