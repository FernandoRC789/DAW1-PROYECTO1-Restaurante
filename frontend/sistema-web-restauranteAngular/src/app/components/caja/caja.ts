import { Component } from '@angular/core';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { SidebarComponent } from '../../layout/sidebar/sidebar';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-caja',
  imports: [CommonModule, NavbarComponent, SidebarComponent],
  templateUrl: './caja.html',
  styleUrl: './caja.css',
})
export class Caja {}
