import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HeaderInner } from './header-inner';

describe('HeaderInner', () => {
  let component: HeaderInner;
  let fixture: ComponentFixture<HeaderInner>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderInner]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HeaderInner);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
