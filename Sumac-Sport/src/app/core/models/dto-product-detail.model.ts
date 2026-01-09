import { Product, ProductVariant } from './product.model';

export interface DtoProductDetail extends Product {
    variants: ProductVariant[];
}
