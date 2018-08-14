import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DigitalfieldComponent } from './digitalfield.component';

describe('DigitalfieldComponent', () => {
  let component: DigitalfieldComponent;
  let fixture: ComponentFixture<DigitalfieldComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DigitalfieldComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DigitalfieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
