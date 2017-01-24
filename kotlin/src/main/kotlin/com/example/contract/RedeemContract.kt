package com.example.contract

import net.corda.core.contracts.*
import net.corda.core.contracts.Requirements.by
import net.corda.core.contracts.clauses.*
import net.corda.core.crypto.SecureHash
import net.corda.core.random63BitValue
import java.text.SimpleDateFormat

/**
 * Created by ravi on 1/24/17.
 */


/**
 * A implementation of a basic smart contract in Corda.
 *
 * This contract facilitates the business logic required for two parties to come to an agreement over a newly issued
 * [RedeemTokenState], which in turn, encapsulates a [TokenOrder].
 *
 * For a new [RedeemTokenState] to be issued onto the ledger a transaction is required which takes:
 * - Zero input states.
 * - One output state: the new [RedeemTokenState].
 * - An Place() command with the public keys of the buyer and seller parties.
 * - A timestamp.
 *
 * The contract code (implemented within the [Timestamped] and [Issue] clauses) is run when the transaction is
 * verified via the verify() function.
 * - [Timestamped] checks for the existence of a timestamp
 * - [Place] runs a series of constraints, see more below
 *
 * All contracts must sub-class the [Contract] interface.
 */
open class RedeemContract() : Contract {

    /**
     * The AllComposition() clause mandates that all specified clauses clauses (in this case [Timestamped] and [Group])
     * must be executed and valid for a transaction involving this type of contract to be valid.
     */
    override fun verify(tx: TransactionForContract) =
            verifyClause(tx, AllComposition(Clauses.Timestamp(), Clauses.Group()), tx.commands.select<Commands>())

    /**
     * Currently this contract only implements one command.
     * If you wish to add further commands to perhaps Amend() or Cancel() a purchase order, you would add them here. You
     * would then need to add associated clauses to handle transaction verification for the new commands.
     */
    interface Commands : CommandData {
        data class Place(override val nonce: Long = random63BitValue()) : IssueCommand, Commands
//        // Additional commands go here.
//        data class Amend(): TypeOnlyCommandData, Commands
    }

    /** This is a reference to the underlying legal contract template and associated parameters. */
    override val legalContractReference: SecureHash = SecureHash.sha256("Course contract template and params")

    /** This is where we implement our clauses. */
    interface Clauses {
        /** Checks for the existence of a timestamp. */
        class Timestamp : Clause<ContractState, Commands, Unit>() {
            override fun verify(tx: TransactionForContract,
                                inputs: List<ContractState>,
                                outputs: List<ContractState>,
                                commands: List<AuthenticatedObject<Commands>>,
                                groupingKey: Unit?): Set<Commands>
            {
                require(tx.timestamp?.midpoint != null) { "must be timestamped" }
                // We return an empty set because we don't process any commands
                return emptySet()
            }
        }

        // If you add additional clauses, make sure to reference them within the 'FirstComposition()' clause.
        class Group : GroupClauseVerifier<RedeemTokenState, Commands, UniqueIdentifier>(FirstComposition(Place())) {
            override fun groupStates(tx: TransactionForContract): List<TransactionForContract.InOutGroup<RedeemTokenState, UniqueIdentifier>>
                    // Group by purchase order linearId for in / out states
                    = tx.groupStates(RedeemTokenState::linearId)
        }

        class Place : Clause<RedeemTokenState, Commands, UniqueIdentifier>() {
            /* This should be moved to outer class */
            val vendorItemMap = hashMapOf<String, Int>(
                    "NFL" to 20,
                    "Movie" to 10,
                    "MOMA" to 3,
                    "Django Unchained" to 5)

            override val requiredCommands: Set<Class<out CommandData>> = setOf(Commands.Place::class.java)

            override fun verify(tx: TransactionForContract,
                                inputs: List<RedeemTokenState>,
                                outputs: List<RedeemTokenState>,
                                commands: List<AuthenticatedObject<Commands>>,
                                groupingKey: UniqueIdentifier?): Set<Commands> {
                val command = tx.commands.requireSingleCommand<Commands.Place>()
                requireThat {
                    // Generic constraints around generation of the issue purchase order transaction.
                    "Input state cannot be empty" by (!inputs.isEmpty())
                    "Only one output state should be created for each group." by (outputs.size == 1)
                    val out = outputs.single()
                    val inp = inputs.single()
                    "The employee and the vendor cannot be the same entity." by (inp.employee != inp.vendor)
                    "All of the participants must be signers." by (command.signers.containsAll(out.participants))

                    // Purchase order specific constraints.
                    // "We only deliver to the UK." by (out.purchaseOrder.deliveryAddress.country == "UK")
                    // "You must order at least one type of item." by (out.purchaseOrder.items.isNotEmpty())
                    //"You cannot order zero or negative amounts of an item." by (out.purchaseOrder.items.map(Item::amount).all { it > 0 })
                    //"You can only order up to 100 items in total." by (out.purchaseOrder.items.map(Item::amount).sum() <= 100)
                    //val time = tx.timestamp?.midpoint
                    //"The delivery date must be in the future." by (out.purchaseOrder.deliveryDate.toInstant() > time)

                    //Earn Token Order constraints
                    "Insufficient funds" by(inp.redeemTokenOrder.taskItem.amount >= vendorItemMap.getOrElse(out.redeemTokenOrder.taskItem.itemName) {0})
                }

                return setOf(command.value)
            }
        }

//        // Additional clauses go here.
//        class Amend : Clause<PurchaseOrderState, Commands, UniqueIdentifier>() {
//            override val requiredCommands: Set<Class<out CommandData>> = setOf(Commands.Amend::class.java)
//
//            override fun verify(tx: TransactionForContract,
//                                inputs: List<PurchaseOrderState>,
//                                outputs: List<PurchaseOrderState>,
//                                commands: List<AuthenticatedObject<Commands>>,
//                                groupingKey: UniqueIdentifier?): Set<Commands> {
//                val command = tx.commands.requireSingleCommand<Commands.Amend>()
//                requireThat {
//                    // Generic constraints around amending purchase orders.
//                    // ...
//                    // Purchase order specific constraints.
//                    // ...
//                }
//                return setOf(command.value)
//            }
//        }
    }
}
