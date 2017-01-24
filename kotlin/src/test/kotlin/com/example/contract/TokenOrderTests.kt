package com.example.contract

import com.example.model.TokenItem
import com.example.model.TokenOrder
import net.corda.core.utilities.TEST_TX_TIME
import net.corda.testing.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.time.Duration
import java.util.*

/**
 * Created by ravi on 1/24/17.
 */

class TokenOrderTests {
    @Test
    fun `transaction must be timestamped`() {
        val tokenItem = TokenItem("course1", SimpleDateFormat("yyyy-MM-dd").parse("2017-01-20"),10)
        val tokenOrder = TokenOrder(1,tokenItem)
        ledger {
            transaction {
                output { EarnTokenState(tokenOrder, ALICE, MEGA_CORP, TaskContract()) }
                `fails with`("must be timestamped")
                timestamp(TEST_TX_TIME)
                command(MEGA_CORP_PUBKEY, ALICE_PUBKEY) { TaskContract.Commands.Place() }
                verifies()
            }
        }
    }

    @Test
    fun `transaction must include Place command`() {
        val tokenItem = TokenItem("course1", SimpleDateFormat("yyyy-MM-dd").parse("2017-01-20"),10)
        val tokenOrder = TokenOrder(1,tokenItem)
        ledger {
            transaction {
                output { EarnTokenState(tokenOrder, ALICE, MEGA_CORP, TaskContract()) }
                timestamp(TEST_TX_TIME)
                fails()
                command(MEGA_CORP_PUBKEY, ALICE_PUBKEY) { TaskContract.Commands.Place() }
                verifies()
            }
        }
    }

    @Test
    fun `Issuer must sign transaction`() {
        val tokenItem = TokenItem("course1", SimpleDateFormat("yyyy-MM-dd").parse("2017-01-20"),10)
        val tokenOrder = TokenOrder(1,tokenItem)
        ledger {
            transaction {
                output { EarnTokenState(tokenOrder, ALICE, MEGA_CORP, TaskContract()) }
                timestamp(TEST_TX_TIME)
                command(ALICE_PUBKEY) { TaskContract.Commands.Place() }
                `fails with`("All of the participants must be signers.")
            }
        }
    }

    @Test
    fun `Employee must sign transaction`() {
        val tokenItem = TokenItem("course1", SimpleDateFormat("yyyy-MM-dd").parse("2017-01-20"),10)
        val tokenOrder = TokenOrder(1,tokenItem)
        ledger {
            transaction {
                output { EarnTokenState(tokenOrder, ALICE, MEGA_CORP, TaskContract()) }
                timestamp(TEST_TX_TIME)
                command(MEGA_CORP_PUBKEY) { TaskContract.Commands.Place() }
                `fails with`("All of the participants must be signers.")
            }
        }
    }

    @Test
    fun `cannot place empty orders`() {
        val tokenItem = TokenItem("course1", SimpleDateFormat("yyyy-MM-dd").parse("2017-01-20"),0)
        val tokenOrder = TokenOrder(1,tokenItem)
        ledger {
            transaction {
                output { EarnTokenState(tokenOrder, ALICE, MEGA_CORP, TaskContract()) }
                timestamp(TEST_TX_TIME)
                command(MEGA_CORP_PUBKEY, ALICE_PUBKEY) { TaskContract.Commands.Place() }
                `fails with`("Token amount for a task must be greater than 0")
            }
        }
    }

    @Test
    fun `cannot place historical orders`() {
        val tokenItem = TokenItem("course1", SimpleDateFormat("yyyy-MM-dd").parse("2017-02-20"),10)
        val tokenOrder = TokenOrder(1,tokenItem)
        ledger {
            transaction {
                output { EarnTokenState(tokenOrder, ALICE, MEGA_CORP, TaskContract()) }
                timestamp(TEST_TX_TIME)
                command(MEGA_CORP_PUBKEY, ALICE_PUBKEY) { TaskContract.Commands.Place() }
                `fails with`("Task completion date must be before due date")
            }
        }
    }
}
