
import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { SidebarComponent } from '../../layout/sidebar/sidebar';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-mesero',
  standalone: true,
  imports: [CommonModule, NavbarComponent, SidebarComponent, RouterModule],
  templateUrl: './mesero.html',
  styleUrl: './mesero.css',
})
export class Mesero {}