import { Component } from '@angular/core';
import { NavbarComponent } from '../../layout/navbar/navbar';
import { SidebarComponent } from '../../layout/sidebar/sidebar';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-admin',
  imports: [CommonModule, NavbarComponent, SidebarComponent,RouterModule],
    templateUrl: './admin.html',
  styleUrl: './admin.css',
})
export class Admin {}
