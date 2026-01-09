import { Component, OnInit } from '@angular/core';
import { loginService } from '../../../core/services/loginService';
import { CommonModule } from '@angular/common';

@Component({
  standalone: true,
  selector: 'app-editar-register',
  imports: [CommonModule],
  templateUrl: './editar-register.html',
  styleUrl: './editar-register.css',
})
export class EditarRegister implements OnInit {

  usuario: any;

  constructor(private LoginService: loginService) {}

  ngOnInit(): void {
    this.usuario = this.LoginService.getUser();
  }
}
