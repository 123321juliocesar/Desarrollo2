import { inject, PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { loginService } from '../services/loginService';

export const authGuard = () => {
    const platformId = inject(PLATFORM_ID);

    // Allow SSR to render the page structure; client-side will verify auth
    if (!isPlatformBrowser(platformId)) {
        return true;
    }

    const authService = inject(loginService);
    const router = inject(Router);

    if (authService.isLoggedIn()) {
        return true;
    }

    // Redirect to login if not authenticated
    router.navigate(['/login']);
    return false;
};
