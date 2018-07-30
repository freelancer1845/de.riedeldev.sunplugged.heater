import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { IocardComponent } from './iocard.component';

describe('IocardComponent', () => {
  let component: IocardComponent;
  let fixture: ComponentFixture<IocardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ IocardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(IocardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
