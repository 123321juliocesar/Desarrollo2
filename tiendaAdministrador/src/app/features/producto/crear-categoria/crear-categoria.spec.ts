import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CrearCategoria } from './crear-categoria';

describe('CrearCategoria', () => {
  let component: CrearCategoria;
  let fixture: ComponentFixture<CrearCategoria>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CrearCategoria]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CrearCategoria);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
