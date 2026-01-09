import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ProductoService } from '../../core/services/producto-service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-producto',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './producto.html',
  styleUrl: './producto.css',
})
export class Producto implements OnInit {

  irCrearProducto() {
    this.router.navigate(['/producto/crear']);
  }

  irCrearCategoria() {
    this.router.navigate(['/categoria/crear']);
  }

  irCrearMarca() {
    this.router.navigate(['/marca/crear']);
  }


  productos: any[] = [];

  constructor(
    private router: Router,
    private productoService: ProductoService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.cargarProductos();
  }

  cargarProductos() {
    this.productoService.obtenerProductos().subscribe({
      next: (data: any) => {
        this.productos = data.products;
        console.log(this.productos);

        // 🔥 Forzar render al cargar
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al obtener productos', err);
      }
    });
  }


  toggleEstado(producto: any): void {
    const nuevoEstado = producto.status === 'active' ? 'inactive' : 'active';

    const productoActualizado = {
      ...producto,
      status: nuevoEstado
    };

    this.productoService.actualizarProducto(producto.id, productoActualizado)
      .subscribe({
        next: () => {
          producto.status = nuevoEstado;

          // 🔥 FORZAR actualización de la vista
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error('Error al cambiar estado', err);
        }
      });
  }

  // --- MODAL DE EDICIÓN ---
  showModal: boolean = false;
  selectedProduct: any = {
    idCategory: '',
    idBrand: '',
    name: '',
    description: '',
    price: 0,
    oldPrice: 0,
    imageUrl: '',
    status: 'active'
  };
  variants: any[] = [];

  // Listas para los selects del modal
  ListarCategorias: any[] = [];
  ListarMarcas: any[] = [];

  // Generador de variantes dentro del modal
  newVariant = {
    color: '',
    colorHex: '#000000',
    sizes: '',
    stock: 0
  };

  // Cloudinary
  private readonly CLOUD_NAME = 'dlvdg1tt6';
  private readonly UPLOAD_PRESET = 'imagen-product';

  cargarListas() {
    this.productoService.ObtenerCategorias().subscribe((data: any) => {
      this.ListarCategorias = data.categories;
    });
    this.productoService.ObtenerMarcas().subscribe((data: any) => {
      this.ListarMarcas = data.brands;
    });
  }

  editarProducto(id: any): void {
    // 1. Cargar listas si no están
    if (this.ListarCategorias.length === 0) this.cargarListas();

    // 2. Obtener detalle del producto (incluyendo variantes)
    this.productoService.obtenerProductoPorId(id).subscribe({
      next: (data: any) => {
        if (data.product) {
          this.selectedProduct = { ...data.product };
          this.variants = data.product.variants || [];
          this.showModal = true;
          this.cdr.detectChanges();
        }
      },
      error: (err) => console.error(err)
    });
  }

  cerrarModal() {
    this.showModal = false;
    this.selectedProduct = {};
    this.variants = [];
  }

  // --- LÓGICA DE VARIANTES ---
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
        idVariant: '', // Nueva variante
        color: this.newVariant.color,
        colorHex: this.newVariant.colorHex,
        size: size,
        stock: this.newVariant.stock
      });
    });

    this.newVariant.sizes = '';
  }

  eliminarVariante(index: number) {
    // Nota: Si la variante tiene ID, el backend intentará borrarla. Si falla por FK, pondrá stock 0.
    // Visualmente la quitamos de la lista aquí.
    this.variants.splice(index, 1);
  }

  // --- SUBIR IMAGEN ---
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
        this.selectedProduct.imageUrl = data.secure_url;
      })
      .catch(err => console.error(err));
  }

  guardarEdicion() {
    if (!this.selectedProduct.name) {
      alert('Nombre es obligatorio');
      return;
    }

    // Preparar payload
    const request = {
      dto: {
        product: this.selectedProduct,
        variants: this.variants
      }
    };

    this.productoService.actualizarProducto(this.selectedProduct.idProduct, request).subscribe({
      next: () => {
        alert('Producto actualizado correctamente');
        this.cerrarModal();
        this.cargarProductos(); // Recargar lista principal
      },
      error: (err) => {
        console.error(err);
        alert('Error al actualizar');
      }
    });
  }

  // 🔹 Eliminar producto
  eliminarProducto(id: number): void {
    const confirmar = confirm('¿Estás seguro de eliminar este producto?');
    if (!confirmar) return;

    this.productoService.eliminarProducto(id).subscribe({
      next: () => {
        this.productos = this.productos.filter(p => p.id !== id);

        // 🔥 Forzar render
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al eliminar producto', err);
      }
    });
  }

}
