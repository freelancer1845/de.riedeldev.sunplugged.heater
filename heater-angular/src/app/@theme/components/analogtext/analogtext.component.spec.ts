import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AnalogtextComponent } from './analogtext.component';

describe('AnalogtextComponent', () => {
  let component: AnalogtextComponent;
  let fixture: ComponentFixture<AnalogtextComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AnalogtextComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AnalogtextComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
