package dev.ajkneisl.backend.db

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val MONGO = KMongo.createClient(
    MongoClientSettings.builder().applyConnectionString(
        ConnectionString(
            dev.ajkneisl.backend.MONGO
        )
    ).retryWrites(false).build()
).coroutine