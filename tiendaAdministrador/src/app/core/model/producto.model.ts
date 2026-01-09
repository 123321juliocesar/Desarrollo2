export interface ProductoModel {
  idCategory: string;
  idBrand: string;
  name: string;
  description: string;
  price: number;
  oldPrice?: number;
  imageUrl?: string;
  status: string;

}
