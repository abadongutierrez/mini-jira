package com.jabaddon.miniprojects.mini_jira.mini_jira_htmx_client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Controller // Add this import statement
import org.springframework.web.bind.annotation.GetMapping // Add this import statement
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.ui.Model // Add this import statement

@SpringBootApplication
class MiniJiraHtmxClientApplication

fun main(args: Array<String>) {
	runApplication<MiniJiraHtmxClientApplication>(*args)
}

@Controller
class IndexController {
	private val cartService: CartService

	constructor(cartService: CartService) {
		this.cartService = cartService
	}

	@GetMapping("/")
	fun index(model: Model): String {
		model.addAttribute("name", "Jabaddon")
		return "index"
	}

	@GetMapping("/error")
	fun error(model: Model): String {
		throw RuntimeException("This is an error")
	}

	@GetMapping("/render-trigger")
	fun renderTrigger(model: Model): String {
		return "render-trigger"
	}

	@GetMapping("/event-trigger")
	fun eventTrigger(model: Model): String {
		return "event-trigger"
	}

	@PostMapping("/render-name")
	fun renderName(model: Model): String {
		model.addAttribute("name", "My name is Jabaddon")
		return "render-name"
	}

	@PostMapping("/render-form")
	fun renderForm(model: Model): String {
		return "render-form"
	}

	@PostMapping("/render-image")
	fun renderImage(model: Model): String {
		model.addAttribute("image-url", "https://imgs.search.brave.com/lKEyqljoY9rsHXc5AyB7ORhEFqP4KFRVdFO5FGWd4O8/rs:fit:500:0:0/g:ce/aHR0cHM6Ly9pLmNo/emJnci5jb20vZnVs/bC85MTk1MDg1MzEy/L2g1Njk4ODRERC9w/cm9ncmFtbWVyLW1l/bWUtdGV4dC1nZXQt/dG9tb3Jyb3dzLWRh/dGUtaW50LWdldHRv/bW9ycm93c2RhdGUt/c2xlZXAxMDAwNjA2/MDI0LWdldGN1cnJl/bnRkYXRl.jpeg")
		return "render-image"
	}

	@PostMapping("/render-table")
	fun renderTable(model: Model): String {
		Thread.sleep(3000)
		model.addAttribute("employees", listOf(
			Employee(1, "Johhn Snow", "King of the North", "Winterfell"),
			Employee(2, "Tyrion Lannister", "Hand of the King", "King's Landing"),
			Employee(3, "Daenerys Targaryen", "Mother of Dragons", "Dragonstone"),
			Employee(4, "Cersei Lannister", "Queen of the Seven Kingdoms", "King's Landing"),
			Employee(5, "Arya Stark", "Assassin", "Winterfell"),
			Employee(6, "Sansa Stark", "Queen of the North", "Winterfell"),
			Employee(7, "Bran Stark", "Three-eyed Raven", "Winterfell"),
			Employee(8, "Samwell Tarly", "Maester", "Oldtown"),
			Employee(9, "Brienne of Tarth", "Knight", "Winterfell"),
			Employee(10, "Davos Seaworth", "Hand of the King", "King's Landing"),
			Employee(11, "Grey Worm", "Commander of the Unsullied", "Dragonstone"),
			Employee(12, "Missandei", "Advisor", "Dragonstone"),
			Employee(13, "Jorah Mormont", "Knight", "Bear Island"),
			Employee(14, "Theon Greyjoy", "Protector of Bran Stark", "Winterfell")
		))
		return "render-table"
	}

	@GetMapping("/render-swap-oob")
	fun renderSwapOob(model: Model): String {
		return "render-swap-oob"
	}

	@GetMapping("/render-shopping-cart")
	fun renderShoppingCart(model: Model): String {
		model.addAttribute("cartItems", cartService.cartItems())
		model.addAttribute("total", cartService.getOrderTotal())
		model.addAttribute("subtotal", cartService.getOrderSubtotal())
		return "render-shopping-cart"
	}

	@PostMapping("/remove-from-cart/{productId}")
	fun removeFromCart(@PathVariable productId: String, model: Model): String {
		cartService.removeCartItemByProductId(productId)
		model.addAttribute("cartItems", cartService.cartItems())
		model.addAttribute("total", cartService.getOrderTotal())
		model.addAttribute("subtotal", cartService.getOrderSubtotal())
		return "render-shopping-cart-oob"
	}
}

data class Employee(
	val id: Int,
	val name: String,
	val position: String,
	val department: String
)
