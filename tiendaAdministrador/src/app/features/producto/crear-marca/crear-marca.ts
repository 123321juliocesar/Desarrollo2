import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MarcaService } from '../../../core/services/marca-service';
@Component({
  selector: 'app-crear-marca',
  standalone: true,
  imports: [CommonModule,FormsModule],
  templateUrl: './crear-marca.html',
  styleUrl: './crear-marca.css',
})
export class CrearMarca {

   marca = this.nuevaMarca();

    constructor(private marcaService: MarcaService) {}


    nuevaMarca() {
      return {
        name: '',
      };
    }


    guardar() {

      if (!this.marca.name.trim()) {
        alert('El nombre es obligatorio');
        return;
      }

      this.marcaService.crearMarca(this.marca).subscribe({
        next: (response) => {
          console.log('Marca creada:', response);
          alert('Marca creada con éxito');


          this.limpiarFormulario();
        },
        error: (error) => {
          console.error('Error al crear categoría:', error);
          alert('Error al crear categoría');
        }
      });
    }


    limpiarFormulario() {
      this.marca = this.nuevaMarca();
    }


}
