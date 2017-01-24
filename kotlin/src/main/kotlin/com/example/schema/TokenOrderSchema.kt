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

object TokenOrderSchema

// TODO: Add schema for purchase order items.
object TokenOrderSchemaV1 : MappedSchema(
        schemaFamily = TokenOrderSchema.javaClass,
        version = 1,
        mappedTypes = listOf(PersistentTokenOrder::class.java)) {
    @Entity
    @Table(name = "token_order_states")
    class PersistentTokenOrder(
            @Column(name = "token_order_id")
            var tokenOrderId: Int,

            @Column(name = "employee_name")
            var employeeName: String,

            @Column(name = "issuer_name")
            var issuerName: String,

            @Column(name = "linear_id")
            var linearId: String,

            @Column(name = "course_complete_date")
            var courseCompleteDate: Date,

            @Column(name = "course_name")
            var courseName: String,

            @Column(name = "token_amount")
            var tokenAmount: Int
    ) : PersistentState()
}
