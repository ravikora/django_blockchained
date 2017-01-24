package com.example.schema

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * Created by ravi on 1/24/17.
 */

object RedeemOrderSchema

// TODO: Add schema for purchase order items.
object RedeemOrderSchemaV1 : MappedSchema(
        schemaFamily = TokenOrderSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentTokenOrder::class.java)) {
    @Entity
    @Table(name = "token_order_states")
    class PersistentTokenOrder(
            @Column(name = "redeem_token_order_id")
            var redeemTokenOrderId: Int,

            @Column(name = "employee_name")
            var employeeName: String,

            @Column(name = "vendor_name")
            var vendorName: String,

            @Column(name = "linear_id")
            var linearId: String,

            @Column(name = "item_name")
            var itemName: String,

            @Column(name = "token_amount")
            var tokenAmount: Int
    ) : PersistentState()
}
