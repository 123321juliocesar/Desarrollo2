import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PedidosCuenta } from './pedidos-cuenta';

describe('PedidosCuenta', () => {
  let component: PedidosCuenta;
  let fixture: ComponentFixture<PedidosCuenta>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PedidosCuenta]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PedidosCuenta);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
