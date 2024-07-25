package com.jabaddon.miniprojects.mini_jira.mini_jira_htmx_client

import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

// Define a data class to represent a product in the store
data class Product(val id: String, val name: String, val price: Double)

// Define a data class to represent an item in the cart
data class CartItem(val product: Product, var quantity: Int)

// Define the CartService class
@Service
class CartService {
	private val products = mutableListOf<Product>()
	private val cartItems = mutableListOf<CartItem>()

	// Method to add a product to the store
	fun addProduct(product: Product) {
		products.add(product)
	}

	// Method to find all products
	fun findAllProducts(): List<Product> {
		return products.toList()
	}

    fun cartItems(): List<CartItem> {
        return cartItems.toList()
    }

	// Method to add an item to the cart
	fun addCartItem(productId: String, quantity: Int) {
		val product = products.find { it.id == productId }
		if (product != null) {
			val cartItem = cartItems.find { it.product.id == productId }
			if (cartItem != null) {
				cartItem.quantity += quantity
			} else {
				cartItems.add(CartItem(product, quantity))
			}
		} else {
			throw IllegalArgumentException("Product not found")
		}
	}

	// Method to remove a product by ID
	fun removeCartItemByProductId(productId: String) {
		cartItems.removeIf { it.product.id == productId }
	}

	// Method to get the order subtotal
	fun getOrderSubtotal(): Double {
        if (cartItems.isEmpty()) {
            return 0.0
        }
		return cartItems.sumOf { it.product.price * it.quantity }
	}

	// Method to get the order total
	fun getOrderTotal(): Double {
		return getOrderSubtotal() + calculateFees()
	}
	
	// Method to calculate fees
	private fun calculateFees(): Double {
		// Add your fee calculation logic here
		return if (getOrderSubtotal() > 0.0) 5.0 else 0.0
	}

    @PostConstruct
    fun init() {
        addProduct(Product("1", "Product 1", 10.0))
        addProduct(Product("2", "Product 2", 20.0))
        addProduct(Product("3", "Product 3", 30.0))

        addCartItem("1", 1)
        addCartItem("2", 2)
        addCartItem("3", 1)
    }
}

