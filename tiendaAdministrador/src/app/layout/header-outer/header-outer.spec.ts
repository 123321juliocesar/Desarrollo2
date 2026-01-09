import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderOuter } from './header-outer';

describe('HeaderOuter', () => {
  let component: HeaderOuter;
  let fixture: ComponentFixture<HeaderOuter>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderOuter]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderOuter);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
