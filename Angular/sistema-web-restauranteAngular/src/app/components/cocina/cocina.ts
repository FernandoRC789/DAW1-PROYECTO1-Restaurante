import { Component } from '@angular/core';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { SidebarComponent } from '../../layout/sidebar/sidebar';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';



@Component({
  selector: 'app-cocina',
  imports: [CommonModule, NavbarComponent, SidebarComponent,RouterModule],
  templateUrl: './cocina.html',
  styleUrl: './cocina.css',
})
export class Cocina {}
