export interface Product {
    idProduct: string;
    idCategory: string;
    idBrand: string;
    name: string;
    description: string;
    price: number;
    oldPrice: number | null;
    imageUrl: string;
    status: string;
    averageRating: number;
    reviewCount: number;
    createdAt: Date;
    updatedAt: Date;
}

export interface DtoProductDetail extends Product {
    variants: ProductVariant[];
}

export type ProductDetail = DtoProductDetail;

export interface ProductVariant {
    idVariant: string;
    idProduct: string;
    size: string | null;
    color: string | null;
    colorHex: string | null;
    stock: number;
    createdAt: Date;
    updatedAt: Date;
}
