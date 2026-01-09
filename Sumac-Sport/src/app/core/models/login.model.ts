import { User } from './user.model';

export interface LoginDTO {
  email: string;
  password: string;
}

export interface LoginRequest {
  dto: LoginDTO;
}

export interface LoginResponse {
  message: string;
  user?: User;
}
