package com.example.plugin

/**
 * Created by ravi on 1/25/17.
 */
import com.esotericsoftware.kryo.Kryo
import com.example.api.TaskApi
import com.example.contract.EarnTokenState
import com.example.contract.TaskContract
import com.example.flow.EarnWorkFlow
import com.example.flow.EarnFlowResult
import com.example.model.TokenItem
import com.example.model.TokenOrder
import com.example.service.TaskService
import net.corda.core.crypto.Party
import net.corda.core.messaging.CordaRPCOps
import net.corda.core.node.CordaPluginRegistry
import net.corda.core.node.PluginServiceHub
import java.util.*
import java.util.function.Function

class TaskPlugin : CordaPluginRegistry() {
    /** A list of classes that expose web APIs. */
    override val webApis: List<Function<CordaRPCOps, out Any>> = listOf(Function(::TaskApi))
    /**
     * A list of flows required for this CorDapp.
     *
     * Any flow which is invoked from from the web API needs to be registered as an entry into this Map. The Map
     * takes the form of:
     *
     *      Name of the flow to be invoked -> Set of the parameter types passed into the flow.
     *
     * E.g. In the case of this CorDapp:
     *
     *      "ExampleFlow.Initiator" -> Set(PurchaseOrderState, Party)
     *
     * This map also acts as a white list. Such that, if a flow is invoked via the API and not registered correctly
     * here, then the flow state machine will _not_ invoke the flow. Instead, an exception will be raised.
     */
    override val requiredFlows: Map<String, Set<String>> = mapOf(
            EarnWorkFlow.Initiator::class.java.name to setOf(EarnTokenState::class.java.name, Party::class.java.name)
    )
    /**
     * A list of long lived services to be hosted within the node. Typically you would use these to register flow
     * factories that would be used when an initiating party attempts to communicate with our node using a particular
     * flow. See the [ExampleService.Service] class for an implementation which sets up a
     */
    override val servicePlugins: List<Function<PluginServiceHub, out Any>> = listOf(Function(TaskService::Service))
    /** A list of directories in the resources directory that will be served by Jetty under /web */
    override val staticServeDirs: Map<String, String> = mapOf(
            // This will serve the exampleWeb directory in resources to /web/example
            "example" to javaClass.classLoader.getResource("exampleWeb").toExternalForm()
    )

    /**
     * Register required types with Kryo (our serialisation framework).
     */
    override fun registerRPCKryoTypes(kryo: Kryo): Boolean {
        kryo.register(EarnTokenState::class.java)
        kryo.register(TaskContract::class.java)
        kryo.register(TokenOrder::class.java)
        kryo.register(Date::class.java)
        kryo.register(TokenItem::class.java)
        kryo.register(EarnFlowResult.Success::class.java)
        kryo.register(EarnFlowResult.Failure::class.java)
        return true
    }
}
