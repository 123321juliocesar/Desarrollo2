import { Component,OnInit,ChangeDetectorRef } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { CategoriaService } from '../../../core/services/categoria-service';
@Component({
  selector: 'app-crear-categoria',
    standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './crear-categoria.html',
  styleUrl: './crear-categoria.css',
})
export class CrearCategoria {

  categoria = this.nuevaCategoria();

  constructor(private categoriaService: CategoriaService) {}


  nuevaCategoria() {
    return {
      name: '',
      description: '',
      productCount: 0
    };
  }


  guardar() {

    if (!this.categoria.name.trim()) {
      alert('El nombre es obligatorio');
      return;
    }

    this.categoriaService.crearCategoria(this.categoria).subscribe({
      next: (response) => {
        console.log('Categoría creada:', response);
        alert('Categoría creada con éxito');


        this.limpiarFormulario();
      },
      error: (error) => {
        console.error('Error al crear categoría:', error);
        alert('Error al crear categoría');
      }
    });
  }


  limpiarFormulario() {
    this.categoria = this.nuevaCategoria();
  }
}
