import { Component, ChangeDetectorRef, OnInit, ViewChild, ElementRef } from '@angular/core';
import { ProductoService } from '../../../core/services/producto-service';
import { FormsModule } from '@angular/forms';
import { CommonModule, NgIf } from '@angular/common';
import { Router } from '@angular/router';


@Component({
  selector: 'app-crear-producto',
  standalone: true,
  imports: [CommonModule, FormsModule, NgIf],
  templateUrl: './crear-producto.html',
  styleUrl: './crear-producto.css',
})
export class CrearProducto implements OnInit {

  ListarCategorias: any[] = [];
  ListarMarcas: any[] = [];

  constructor(
    private router: Router,
    private productoService: ProductoService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarCategorias();
    this.cargarMarcas();
  }
  cargarMarcas() {
    this.productoService.ObtenerMarcas().subscribe({
      next: (data: any) => {
        this.ListarMarcas = data.brands;
        console.log(this.ListarMarcas);

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener marcas', err);
      }

    })
  }



  cargarCategorias() {
    this.productoService.ObtenerCategorias().subscribe({
      next: (data: any) => {
        this.ListarCategorias = data.categories;
        console.log(this.ListarCategorias);

        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener productos', err);
      }
    });
  }




  producto = {
    idCategory: '',
    idBrand: '',
    name: '',
    description: '',
    price: 0,
    oldPrice: 0,
    imageUrl: '',
    status: 'active'
  };

  private readonly CLOUD_NAME = 'dlvdg1tt6';
  private readonly UPLOAD_PRESET = 'imagen-product';

  subirImagen(event: any) {
    const file = event.target.files[0];

    if (!file) return;

    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', this.UPLOAD_PRESET);

    fetch(`https://api.cloudinary.com/v1_1/${this.CLOUD_NAME}/image/upload`, {
      method: 'POST',
      body: formData
    })
      .then(res => res.json())
      .then(data => {
        this.producto.imageUrl = data.secure_url;
        console.log('Imagen subida:', data.secure_url);
      })
      .catch(err => console.error('Error Cloudinary', err));
  }

  // Variantes
  variants: any[] = [];

  // Generador de variantes
  newVariant = {
    color: '',
    colorHex: '#000000',
    sizes: '', // Comma separated, e.g. "S,M,L"
    stock: 0
  };

  agregarVariantesEnBloque() {
    if (!this.newVariant.color || !this.newVariant.sizes) {
      alert('Color y Tallas son obligatorios');
      return;
    }

    const sizes = this.newVariant.sizes.split(',').map(s => s.trim()).filter(s => s !== '');

    if (sizes.length === 0) {
      alert('Ingrese al menos una talla válida');
      return;
    }

    sizes.forEach(size => {
      this.variants.push({
        color: this.newVariant.color,
        colorHex: this.newVariant.colorHex,
        size: size,
        stock: this.newVariant.stock
      });
    });

    this.newVariant.sizes = '';
  }

  eliminarVariante(index: number) {
    this.variants.splice(index, 1);
  }

  guardar() {
    if (!this.producto.idCategory || !this.producto.idBrand) {
      alert('Debe seleccionar categoría y marca');
      return;
    }

    if (!this.producto.name || this.producto.price <= 0) {
      alert('Nombre y precio son obligatorios');
      return;
    }

    if (!this.producto.imageUrl) {
      alert('Sube una imagen');
      return;
    }

    const productoEnviar = { ...this.producto };

    // Preparar variantes para el backend
    const variantsEnviar = this.variants.map(v => ({
      ...v,
      idProduct: '' // El backend lo asignará al crear el producto
    }));

    const request = {
      dto: {
        product: productoEnviar,
        variants: variantsEnviar
      }
    };

    // Cast request to any if Interface doesn't match perfectly yet, or allow TS to check
    this.productoService.crearProducto(request as any).subscribe({
      next: () => {
        alert('Producto creado correctamente con ' + this.variants.length + ' variantes');
        this.limpiarFormulario();
      },
      error: (err) => {
        console.error(err);
        alert('Error al crear producto');
      }
    });
  }

  @ViewChild('fileInput') fileInput: ElementRef | undefined;

  limpiarFormulario() {
    this.producto = {
      idCategory: '',
      idBrand: '',
      name: '',
      description: '',
      price: 0,
      oldPrice: 0,
      imageUrl: '',
      status: 'active'
    };
    this.variants = [];
    this.newVariant = {
      color: '',
      colorHex: '#000000',
      sizes: '',
      stock: 0
    };

    if (this.fileInput) {
      this.fileInput.nativeElement.value = '';
    }
  }
}
