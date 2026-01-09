import { Product, ProductVariant } from './product.model';

export interface CartItem {
    idCartItem: string;
    idCart: string;
    idProduct: string;
    idVariant: string;
    quantity: number;
    unitPrice: number;
    createdAt: string;
    updatedAt: string;
    product: Product;
    variant: ProductVariant;
}

export interface Cart {
    idCart: string;
    idUser: string;
    createdAt: string;
    updatedAt: string;
    items: CartItem[];
    totalItems: number;
    subtotal: number;
    shippingCost: number;
    total: number;
}

export interface AddToCartRequest {
    idUser: string;
    idProduct: string;
    idVariant: string;
    quantity: number;
}

export interface UpdateCartItemRequest {
    idUser: string;
    quantity: number;
}
