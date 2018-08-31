import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MappedioComponent } from './mappedio.component';

describe('MappedioComponent', () => {
  let component: MappedioComponent;
  let fixture: ComponentFixture<MappedioComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MappedioComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MappedioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
