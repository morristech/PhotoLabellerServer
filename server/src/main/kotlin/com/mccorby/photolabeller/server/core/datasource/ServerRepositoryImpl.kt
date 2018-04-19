package com.mccorby.photolabeller.server.core.datasource

import com.mccorby.photolabeller.server.core.domain.model.ClientUpdate
import com.mccorby.photolabeller.server.core.domain.model.UpdatingRound
import com.mccorby.photolabeller.server.core.domain.repository.ServerRepository
import java.io.File

class ServerRepositoryImpl(private val fileDataSource: FileDataSource, private val memoryDataSource: MemoryDataSource): ServerRepository {
    override fun listClientUpdates(): List<ClientUpdate> = memoryDataSource.getUpdates()

    override fun storeClientUpdate(updateByteArray: ByteArray, samples: Int) {
        val file = fileDataSource.storeUpdate(updateByteArray)
        memoryDataSource.addUpdate(ClientUpdate(file, samples))
    }

    override fun getTotalSamples(): Int {
        return listClientUpdates().map { it.samples }.sum()
    }

    override fun clearClientUpdates(): Boolean {
        memoryDataSource.clear()
        fileDataSource.clearUpdates()
        return true
    }

    override fun storeCurrentUpdatingRound(updatingRound: UpdatingRound) {
        fileDataSource.saveUpdatingRound(updatingRound)
    }

    override fun retrieveCurrentUpdatingRound(): UpdatingRound {
        return fileDataSource.retrieveCurrentUpdatingRound()
    }

    override fun retrieveModel(): File {
        return fileDataSource.retrieveModel()
    }

    // TODO This is just for testing purposes. Number of samples should be serialised together with the file
    override fun restoreClientUpdates() {
         fileDataSource.getClientUpdates().forEach {
             memoryDataSource.addUpdate(ClientUpdate(it, 32))
         }
    }
}