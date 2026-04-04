package com.openmobilehub.android.storage.plugin.box.restful

import com.openmobilehub.android.auth.core.OmhAuthClient
import com.openmobilehub.android.storage.core.OmhStorageClient
import com.openmobilehub.android.storage.core.OmhStorageFactory
import com.openmobilehub.android.storage.plugin.box.restful.data.repository.BoxRestfulFileRepository
import com.openmobilehub.android.storage.plugin.box.restful.data.retrofit.BoxRetrofitImpl

class BoxRestfulOmhStorageClientFactory : OmhStorageFactory {
    override fun getStorageClient(authClient: OmhAuthClient): OmhStorageClient {
        val retrofit = BoxRetrofitImpl(authClient)
        val repository = BoxRestfulFileRepository(
            retrofit.boxApiService,
            retrofit.boxUploadApiService,
            retrofit.boxCollaborationApiService
        )
        return BoxRestfulOmhStorageClient(authClient, repository)
    }
}
