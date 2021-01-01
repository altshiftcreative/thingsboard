import {Component, Inject} from '@angular/core';
import {MatDialog, MAT_DIALOG_DATA} from '@angular/material/dialog';

export interface DialogData {
  animal: 'panda' | 'unicorn' | 'lion';
}

/**
 * @title Injecting data when opening a dialog
 */

@Component({
  selector: 'popup-pogrss',
  templateUrl: 'popup-show.html',
})
export class DialogAlert {
  constructor(@Inject(MAT_DIALOG_DATA) public data: DialogData) {}
}
