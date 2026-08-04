import { Component , ViewEncapsulation} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'],
  encapsulation: ViewEncapsulation.Emulated
})
export class LoginComponent {

  username = '';
  password = '';

  constructor(private authService: AuthService) {}

  login() {
    Swal.fire({
      title: 'Iniciando sesión...',
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });

    this.authService.login(this.username, this.password).subscribe({
          next: (data) => {

      Swal.close();

      Swal.fire({
        icon: 'success',
        title: 'Bienvenido',
        text: 'Acceso correcto',
        timer: 1200,
        showConfirmButton: false
      });

      // aquí normalmente haces redirect o guardas token
    },
      error: () => {
        Swal.fire({
          icon: 'error',
          title: 'Login fallido',
          text: 'Usuario o contraseña incorrectos'
        });
      }
    });
  }
}