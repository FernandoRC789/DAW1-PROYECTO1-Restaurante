import { Component, signal } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common'; // 🔥 IMPORTANTE


@Component({
  selector: 'app-root',
  imports: [RouterOutlet,CommonModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('sistema-web-restaurante');

  constructor(private router: Router) {} // 🔥 FALTABA ESTO

isLoginRoute() {
  return this.router.url.includes('login');
}
}
