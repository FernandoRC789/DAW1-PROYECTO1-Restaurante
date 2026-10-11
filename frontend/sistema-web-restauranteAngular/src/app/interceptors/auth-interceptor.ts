import { HttpInterceptorFn } from '@angular/common/http';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  // Obtener el token JWT del localStorage
  const token = localStorage.getItem('token');

  // Si existe el token, agregarlo al header Authorization
  if (token) {
    const cloned = req.clone({
      withCredentials: true,
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next(cloned);
  }

  // Si no hay token, proceder con la petición original
  return next(req);
};