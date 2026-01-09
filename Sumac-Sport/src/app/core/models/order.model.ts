export enum OrderStatus {
    PENDING = 'pending',
    CONFIRMED = 'confirmed',
    SHIPPED = 'shipped',
    DELIVERED = 'delivered',
    CANCELLED = 'cancelled'
}

export interface OrderItem {
    idOrderItem: string;
    idOrder: string;
    idProduct: string;
    idVariant: string | null;
    quantity: number;
    unitPrice: number;

    // Product details
    productName: string;
    productImageUrl: string;

    // Variant details
    variantSize?: string;
    variantColor?: string;
    variantColorHex?: string;
}

export interface Order {
    idOrder: string;
    idUser: string;
    orderNumber: string;
    orderDate: string;
    totalAmount: number;
    subtotal: number;
    shippingCost: number;
    status: OrderStatus | string;
    shippingAddress: string;
    shippingCity: string;
    shippingPostalCode: string;
    shippingProvince: string;
    shippingCountry: string;
    createdAt: string;
    updatedAt: string;

    // Extended fields
    items: OrderItem[];
    totalItems: number;
}

export interface OrderStatusInfo {
    status: OrderStatus;
    label: string;
    icon: string;
    color: string;
}
