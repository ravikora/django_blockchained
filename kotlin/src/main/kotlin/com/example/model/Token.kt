package com.example.model

import java.util.*

/**
 * A simple class representing a purchase order.
 * @param orderNumber the purchase order's id number.
 * @param expiryDate the requested deliveryDate.
 * @param issueDate the delivery address.
 */
data class Token(val tokenId: Int,
                 val expiryDate: Date,
                 val issueDate: Date
                 )