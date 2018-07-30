import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalogInputsComponent } from './analog-inputs.component';

describe('AnalogInputsComponent', () => {
  let component: AnalogInputsComponent;
  let fixture: ComponentFixture<AnalogInputsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnalogInputsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalogInputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
