/*
 * Copyright 2023 Open Mobile Hub
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("MaxLineLength")

package com.openmobilehub.android.storage.plugin.googledrive.gms.data.mapper

import com.openmobilehub.android.storage.core.model.OmhCreatePermission
import com.openmobilehub.android.storage.core.model.OmhPermissionRole
import com.openmobilehub.android.storage.plugin.googledrive.gms.GoogleDriveGmsConstants.ANYONE_TYPE
import com.openmobilehub.android.storage.plugin.googledrive.gms.GoogleDriveGmsConstants.DOMAIN_TYPE
import com.openmobilehub.android.storage.plugin.googledrive.gms.GoogleDriveGmsConstants.GROUP_TYPE
import com.openmobilehub.android.storage.plugin.googledrive.gms.GoogleDriveGmsConstants.USER_TYPE
import com.openmobilehub.android.storage.plugin.googledrive.gms.GoogleDriveGmsConstants.WRITER_ROLE
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.repository.testdoubles.TEST_PERMISSION_DOMAIN
import com.openmobilehub.android.storage.plugin.googledrive.gms.data.repository.testdoubles.TEST_PERMISSION_EMAIL_ADDRESS
import org.junit.Test
import kotlin.test.assertEquals

internal class DataMappersTest {

    @Test
    fun `given a AnyonePermission, when toPermission is called, then a Permission with corresponding fields is returned`() {
        val anyonePermission = OmhCreatePermission.AnyonePermission(OmhPermissionRole.WRITER)

        val result = anyonePermission.toPermission()

        assertEquals(ANYONE_TYPE, result.type)
        assertEquals(WRITER_ROLE, result.role)
    }

    @Test
    fun `given a DomainPermission, when toPermission is called, then a Permission with corresponding fields is returned`() {
        val domainPermission =
            OmhCreatePermission.DomainPermission(OmhPermissionRole.WRITER, TEST_PERMISSION_DOMAIN)

        val result = domainPermission.toPermission()

        assertEquals(DOMAIN_TYPE, result.type)
        assertEquals(WRITER_ROLE, result.role)
        assertEquals(TEST_PERMISSION_DOMAIN, result.domain)
    }

    @Test
    fun `given a GroupPermission, when toPermission is called, then a Permission with corresponding fields is returned`() {
        val groupPermission = OmhCreatePermission.GroupPermission(
            OmhPermissionRole.WRITER,
            TEST_PERMISSION_EMAIL_ADDRESS
        )

        val result = groupPermission.toPermission()

        assertEquals(GROUP_TYPE, result.type)
        assertEquals(WRITER_ROLE, result.role)
        assertEquals(TEST_PERMISSION_EMAIL_ADDRESS, result.emailAddress)
    }

    @Test
    fun `given a UserPermission, when toPermission is called, then a Permission with corresponding fields is returned`() {
        val groupPermission = OmhCreatePermission.UserPermission(
            OmhPermissionRole.WRITER,
            TEST_PERMISSION_EMAIL_ADDRESS
        )

        val result = groupPermission.toPermission()

        assertEquals(USER_TYPE, result.type)
        assertEquals(WRITER_ROLE, result.role)
        assertEquals(TEST_PERMISSION_EMAIL_ADDRESS, result.emailAddress)
    }
}
