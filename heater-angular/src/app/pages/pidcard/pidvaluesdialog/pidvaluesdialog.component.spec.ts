import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PidvaluesdialogComponent } from './pidvaluesdialog.component';

describe('PidvaluesdialogComponent', () => {
  let component: PidvaluesdialogComponent;
  let fixture: ComponentFixture<PidvaluesdialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PidvaluesdialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PidvaluesdialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
