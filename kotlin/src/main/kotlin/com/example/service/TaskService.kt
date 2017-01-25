package com.example.service

import com.example.flow.EarnWorkFlow
import net.corda.core.node.PluginServiceHub

/**
 * Created by ravi on 1/25/17.
 */


/**
 * This service registers a flow factory we wish to use when a initiating party attempts to communicate with us
 * using a particular flow. Registration is done against a marker class (in this case [EarnWorkFlow.Initiator]
 * which is sent in the session handshake by the other party. If this marker class has been registered then the
 * corresponding factory will be used to create the flow which will communicate with the other side. If there is no
 * mapping then the session attempt is rejected.
 *
 * In short, this bit of code is required for the seller in this Example scenario to repond to the buyer using the
 * [EarnWorkFlow.Acceptor] flow.
 */
object TaskService {
    class Service(services: PluginServiceHub) {
        init {
            services.registerFlowInitiator(EarnWorkFlow.Initiator::class) { EarnWorkFlow.Acceptor(it) }
            services.registerFlowInitiator(EarnWorkFlow.Initiator::class) { EarnWorkFlow.Acceptor(it) }
        }
    }
}