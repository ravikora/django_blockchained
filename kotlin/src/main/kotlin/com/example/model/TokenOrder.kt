package com.example.model

import java.util.*

/**
 * The name, amount and price of an item to be purchased. It is assumed that the buyer has the seller's item catalogue
 * and will only place orders for valid items. Of course, a reference to a particular version of the catalogue could be
 * included with the Issue() purchase order transaction as an attachment, such that the seller can check the items are valid.
 * For more details on attachments see
 *
 * samples/attachment-demo/src/kotlin/net/corda/attachmentdemo
 *
 * in the main Corda repo (http://github.com/corda/corda).
 *
 * In the contract verify code, we have written some constraints about items.
 * @param the name of the item to be delivered
 * @param amount the amount of an item to be delivered
 */
data class TokenItem(val courseName: String, val completeDate: Date, val amount: Int)


/**
 * A simple class representing a purchase order.
 * @param orderNumber the purchase order's id number.
 * @param expiryDate the requested deliveryDate.
 * @param issueDate the delivery address.
 */
data class TokenOrder(val tokenOrderId: Int,
                 val taskItem: TokenItem
                 )