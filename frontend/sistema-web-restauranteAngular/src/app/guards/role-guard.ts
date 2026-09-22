import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const roleGuard = (roles: string[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);

    const userRol = auth.getRol();

    if (!userRol || !roles.includes(userRol)) {
      router.navigate(['/login']);
      return false;
    }

    return true;
  };
};