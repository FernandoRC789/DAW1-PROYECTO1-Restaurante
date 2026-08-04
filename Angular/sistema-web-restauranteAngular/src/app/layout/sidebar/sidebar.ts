import { Component } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';


@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: 'sidebar.html'
})
export class SidebarComponent {

  rol: string | null = '';

  constructor(private auth: AuthService) {
    this.rol = this.auth.getRol();
  }
}