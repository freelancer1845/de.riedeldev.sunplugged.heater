import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HeateroverviewComponent } from './heateroverview.component';

describe('HeateroverviewComponent', () => {
  let component: HeateroverviewComponent;
  let fixture: ComponentFixture<HeateroverviewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ HeateroverviewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HeateroverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
