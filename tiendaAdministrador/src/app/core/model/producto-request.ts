import { ProductoModel } from "./producto.model";

export interface ProductoCreateRequest {
  dto: {
    product: ProductoModel;
  };
}
