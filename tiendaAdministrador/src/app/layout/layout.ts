import { Component } from '@angular/core';
import { HeaderInner } from './header-inner/header-inner';
import { HeaderOuter } from './header-outer/header-outer';
import { Sidebar } from './sidebar/sidebar';
import { FooterInner } from './footer-inner/footer-inner';
import { FooterOuter } from './footer-outer/footer-outer';
import { RouterOutlet } from '@angular/router';
@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [HeaderInner, HeaderOuter, Sidebar, FooterInner, FooterOuter, RouterOutlet],
  templateUrl: './layout.html',
  styleUrl: './layout.css',
})
export class Layout {
   sidebarOpen = true;

   toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

}
