import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PidcardComponent } from './pidcard.component';

describe('PidcardComponent', () => {
  let component: PidcardComponent;
  let fixture: ComponentFixture<PidcardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PidcardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PidcardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
