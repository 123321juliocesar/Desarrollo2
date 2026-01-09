export interface User {
    idUser: string;
    idRole: string;
    firstName: string;
    lastName: string;
    email: string;
    password?: string;
    phone?: string;
    address?: string;
    city?: string;
    postalCode?: string;
    province?: string;
    country?: string;
    birthDate?: string;
    active: boolean;
}
