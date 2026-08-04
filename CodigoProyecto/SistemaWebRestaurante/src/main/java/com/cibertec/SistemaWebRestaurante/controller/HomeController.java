/*package com.cibertec.SistemaWebRestaurante.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;

@Controller
public class HomeController {
	@GetMapping("/")
	public String inicio() {
		return "inicio"; // templates/inicio.html
	}

	@GetMapping("/admin")
	public String admin() {
		return "admin"; // templates/admin.html
	}

	@GetMapping("/login")
	public String login() {
		return "login"; // templates/login.html
	}

	@GetMapping("/publico")
	public String publico() {
		return "publico"; // templates/publico.html
	}
	
	@GetMapping("/pedidos-view")
	public String pedidos() {
		return "pedidos"; // templates/pedidos.html
	}
	
	@GetMapping("/detalle-pedidos")
	public String detalle_pedidos() {
		return "detalle_pedidos"; // templates/detalle_pedidos.html
	}
	
	@GetMapping("/mesero")
	public String mesero(Model model) {

	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    String username = auth.getName(); // 👈 aquí obtienes el usuario

	    model.addAttribute("usuario", username);

	    return "mesero"; // tu HTML	}
	}
	@GetMapping("/cocina")
	public String cocina(Model model) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    String username = auth.getName(); // 👈 aquí obtienes el usuario

	    model.addAttribute("usuario", username);
		return "cocina"; // templates/cocina.html
	}
	
	@GetMapping("/caja")
	public String caja(Model model) {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

	    String username = auth.getName(); // 👈 aquí obtienes el usuario

	    model.addAttribute("usuario", username);
		return "caja"; // templates/caja.html
	}
}*/
