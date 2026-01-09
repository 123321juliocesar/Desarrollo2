import { Usuario } from "./usuario.model";

export interface RegisterRequest {
  type: string;
  listMessage: string[];
  user: Usuario;
}
