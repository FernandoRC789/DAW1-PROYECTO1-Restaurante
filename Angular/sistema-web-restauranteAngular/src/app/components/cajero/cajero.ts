import { Component } from '@angular/core';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { SidebarComponent } from '../../layout/sidebar/sidebar';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-caja',
  standalone: true,
  imports: [CommonModule, NavbarComponent, SidebarComponent,RouterOutlet],
  templateUrl: './cajero.html',
  styleUrl: './cajero.css',
})
export class Caja {}
