import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FirmwareFilesComponent } from './firmware-files.component';

describe('FirmwareFilesComponent', () => {
  let component: FirmwareFilesComponent;
  let fixture: ComponentFixture<FirmwareFilesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ FirmwareFilesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FirmwareFilesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
