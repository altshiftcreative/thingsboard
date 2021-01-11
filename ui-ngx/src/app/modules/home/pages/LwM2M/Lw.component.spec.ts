import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LwComponent } from './Lw.component';

describe('LwComponent', () => {
  let component: LwComponent;
  let fixture: ComponentFixture<LwComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LwComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LwComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
