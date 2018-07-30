import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalogOutputsComponent } from './analog-outputs.component';

describe('AnalogOutputsComponent', () => {
  let component: AnalogOutputsComponent;
  let fixture: ComponentFixture<AnalogOutputsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnalogOutputsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalogOutputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
