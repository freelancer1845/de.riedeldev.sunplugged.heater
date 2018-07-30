import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DigitalOutputsComponent } from './digital-outputs.component';

describe('DigitalOutputsComponent', () => {
  let component: DigitalOutputsComponent;
  let fixture: ComponentFixture<DigitalOutputsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DigitalOutputsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DigitalOutputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
