import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DigitalInputsComponent } from './digital-inputs.component';

describe('DigitalInputsComponent', () => {
  let component: DigitalInputsComponent;
  let fixture: ComponentFixture<DigitalInputsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DigitalInputsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DigitalInputsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
