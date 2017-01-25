package com.example.api

import net.corda.core.messaging.startFlow
import com.example.contract.EarnTokenState
import com.example.contract.TaskContract
import com.example.flow.EarnFlowResult
import com.example.flow.EarnWorkFlow.Initiator
import com.example.model.TokenOrder
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.messaging.startFlow
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Created by sergeyfogelson on 1/25/17.
 */

val TASK_NOTARY_NAME = "Controller"

// This API is accessible from /api/example. All paths specified below are relative to it.
@javax.ws.rs.Path("example")
class TaskApi(val services: net.corda.core.messaging.CordaRPCOps) {
    val myLegalName: String = services.nodeIdentity().legalIdentity.name

    /**
     * Returns the party name of the node providing this end-point.
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("me")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    fun whoami() = mapOf("me" to myLegalName)

    /**
     * Returns all parties registered with the [NetworkMapService], the names can be used to look-up identities
     * by using the [IdentityService].
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("peers")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    fun getPeers() = mapOf("peers" to services.networkMapUpdates().first
            .map { it.legalIdentity.name }
            .filter { it != myLegalName && it != TASK_NOTARY_NAME })

    /**
     * Displays all purchase order states that exist in the vault.
     */
    @javax.ws.rs.GET
    @javax.ws.rs.Path("token-orders")
    @javax.ws.rs.Produces(javax.ws.rs.core.MediaType.APPLICATION_JSON)
    fun getTokenOrders() = services.vaultAndUpdates().first

    /**
     * This should only be called from the 'buyer' node. It initiates a flow to agree a purchase order with a
     * seller. Once the flow finishes it will have written the purchase order to ledger. Both the buyer and the
     * seller will be able to see it when calling /api/example/purchase-orders on their respective nodes.
     *
     * This end-point takes a Party name parameter as part of the path. If the serving node can't find the other party
     * in its network map cache, it will return an HTTP bad request.
     *
     * The flow is invoked asynchronously. It returns a future when the flow's call() method returns.
     */

    @PUT
    @Path("{party}/create-token-order")
    fun createTokenOrder(tokenOrder: TokenOrder, @PathParam("party") partyName: String): Response {
        val otherParty = services.partyFromName(partyName)
        if (otherParty == null) {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        val state = EarnTokenState(
                tokenOrder,
                services.nodeIdentity().legalIdentity,
                otherParty,
                TaskContract())

        // The line below blocks and waits for the future to resolve.
        val result: EarnFlowResult = services
                .startFlow(::Initiator, state, otherParty)
                .returnValue
                .toBlocking()
                .first()

        when (result) {
            is EarnFlowResult.Success ->
                return Response
                        .status(Response.Status.CREATED)
                        .entity(result.message)
                        .build()
            is EarnFlowResult.Failure ->
                return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(result.message)
                        .build()
        }
    }
}