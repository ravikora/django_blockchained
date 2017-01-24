package com.example.model

import java.util.*

/**
 * Created by ravi on 1/24/17.
 */


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
 * @param itemName name of the item to be delivered
 * @param amount the amount of an item to be delivered
 */
data class RedeemTokenItem(val itemName: String, val amount: Int)


/**
 * A simple class representing a purchase order.
 * @param redeemTokenOrderId the purchase order's id number.
 * @param taskItem the requested deliveryDate.
 */
data class RedeemTokenOrder(val redeemTokenOrderId: Int,
                      val taskItem: RedeemTokenItem
)