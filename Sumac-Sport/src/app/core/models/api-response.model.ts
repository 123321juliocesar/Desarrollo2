import { Product, ProductDetail } from './product.model';
import { Brand } from './brand.model';
import { Category } from './category.model';

//Respuesta genérica del API
export interface ApiResponse {
    type: string;
    listMessage: string[];
}


 //Respuesta de lista de productos
export interface ProductListResponse extends ApiResponse {
    products: Product[];
}


//Respuesta de un solo producto
export interface ProductResponse extends ApiResponse {
    product: Product;
}

//Respuesta de detalle de producto con variantes
export interface ProductDetailResponse extends ApiResponse {
    product: ProductDetail;
}

//Respuesta de una sola marca
export interface BrandResponse extends ApiResponse {
    brand: Brand;
}

//Respuesta de lista de marcas
export interface BrandListResponse extends ApiResponse {
    brands: Brand[];
}


//Respuesta de lista de categorías
export interface CategoryListResponse extends ApiResponse {
    categories: Category[];
}

//Resultado de toggle favorito
export interface FavoriteToggleResult {
    idFavorite: string | null;
    action: string; // 'added' or 'removed'
    message: string;
    error?: string;
}

//Respuesta de operación de favorito
export interface FavoriteResponse extends ApiResponse {
    action?: string;
    favorite?: any; // DtoFavorite
}

///Respuesta de verificación de favorito
export interface FavoriteCheckResponse extends ApiResponse {
    favorite: boolean; // true si es favorito, false si no
}


export interface DtoFavoriteWithProduct {
    idFavorite: string;
    idUser: string;
    idProduct: string;
    product: Product;
}

export interface FavoriteListResponse extends ApiResponse {
    favorites: DtoFavoriteWithProduct[];
}
