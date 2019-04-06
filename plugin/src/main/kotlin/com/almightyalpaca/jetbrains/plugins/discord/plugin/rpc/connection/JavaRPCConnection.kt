//package com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.connection
//
//import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logger
//import com.almightyalpaca.jetbrains.plugins.discord.plugin.logging.Logging
//import com.almightyalpaca.jetbrains.plugins.discord.plugin.rpc.RichPresence
//import com.jagrosh.discordipc.IPCClient
//import com.jagrosh.discordipc.entities.DiscordBuild
//import kotlinx.coroutines.*
//import kotlin.coroutines.CoroutineContext
//
//class JavaRPCConnection(override val appId: Long) : RPCConnection, CoroutineScope {
//    private val parentJob: Job by lazy { Job() }
//    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Default + parentJob }
//
//    private var rpc: IPCClient = IPCClient(appId)
//
//    private var updateJob: Job? = null
//
//    // TODO: handle rpc connection failure
//    @Synchronized
//    override fun connect() {
//        Logger.Level.TRACE { "RPCConnection($appId)#connect()" }
//
//        // TODO: get user from ready
////        rpc.setListener(object :IPCListener {
////            override fun onReady(client: IPCClient?) {
////                super.onReady(client)
////            }
////        })
//
//        rpc.connect(DiscordBuild.ANY)
//    }
//
//    @Synchronized
//    override fun send(presence: RichPresence?) {
//        Logger.Level.TRACE { "RPCConnection($appId)#send()" }
//
//        updateJob?.cancel()
//
//        updateJob = launch {
//            delay(UPDATE_DELAY)
//
//            rpc.sendRichPresence(presence?.toLibraryObject())
//        }
//    }
//
//    @Synchronized
//    override fun disconnect() {
//        Logger.Level.TRACE { "RPCConnection($appId)#disconnect()" }
//
//        parentJob.cancel()
//
//        rpc.close()
//    }
//
//    companion object : Logging()
//}
//
//private fun RichPresence.toLibraryObject() = com.jagrosh.discordipc.entities.RichPresence.Builder().apply builder@{
//    this@builder.setState(state)
//    this@builder.setDetails(details)
//    this@builder.setStartTimestamp(startTimestamp)
//    this@builder.setEndTimestamp(endTimestamp)
//    this@builder.setLargeImage(largeImage?.key, largeImage?.text)
//    this@builder.setSmallImage(smallImage?.key, smallImage?.text)
//    this@builder.setParty(partyId, partySize, partyMax)
//    this@builder.setMatchSecret(matchSecret)
//    this@builder.setJoinSecret(joinSecret)
//    this@builder.setSpectateSecret(spectateSecret)
//    this@builder.setInstance(instance)
//}.build()